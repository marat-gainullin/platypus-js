package com.eas.client.application;

import java.util.ArrayList;
import java.util.List;

import com.bearsoft.rowset.Row;
import com.bearsoft.rowset.Rowset;
import com.bearsoft.rowset.RowsetCallbackAdapter;
import com.bearsoft.rowset.changes.Change;
import com.bearsoft.rowset.dataflow.DelegatingFlowProvider;
import com.bearsoft.rowset.metadata.DataTypeInfo;
import com.bearsoft.rowset.metadata.Field;
import com.bearsoft.rowset.metadata.Fields;
import com.eas.client.CancellableCallback;
import com.eas.client.StringCallbackAdapter;
import com.eas.client.application.AppClient;
import com.eas.client.model.ModelBaseTest;
import com.eas.client.queries.Query;
import com.eas.client.queries.QueryCallbackAdapter;
import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.user.client.Timer;

public class DataFlowTest extends GWTTestCase {

	@Override
	public String getModuleName() {
		return "com.eas.client.application.Main";
	}

	@Override
	protected void gwtSetUp() throws Exception {
		super.gwtSetUp();
		delayTestFinish(60 * 60 * 1000);
		Timer timer = new Timer() {
			public void run() {
				// check test's internal counters
				fail("Test is time-out");
				finishTest();
			}
		};
		// Schedule the event and return control to the test system.
		timer.schedule(20 * 1000);
	}

	public static final String AMBIGUOUS_QUERY_ID = "134564170799279";
	public static final String COMMAND_QUERY_ID = "134570075809763";
	public static final Double NEW_RECORD_ID = Double.valueOf(4125.0);
	public static final String NEW_RECORD_NAME_G = "test gname";
	public static final String NEW_RECORD_NAME_T = "test tname";
	public static final String NEW_RECORD_NAME_K = "test kname";

	public void testThreeTablesTest() throws Exception {
		final List<Change> commonLog = new ArrayList<Change>();
		final AppClient client = ModelBaseTest.initDevelopTestClient(getModuleName());
		client.getAppQuery(AMBIGUOUS_QUERY_ID, new QueryCallbackAdapter() {
			@Override
			public void run(Query aQuery) throws Exception {
				Query query = aQuery;
				query.execute(new RowsetCallbackAdapter() {

					@Override
					public void doWork(Rowset aRowset) throws Exception {
						final Rowset rowset = aRowset;
						rowset.setFlowProvider(new DelegatingFlowProvider(rowset.getFlowProvider()){
							@Override
							public List<Change> getChangeLog() {
							    return commonLog;
							}
						});
						final int oldRowsetSize = rowset.size();
						assertTrue(oldRowsetSize > 1);
						final Fields fields = rowset.getFields();
						Row row = new Row(fields);
						row.setColumnObject(fields.find("tid"), NEW_RECORD_ID);
						Field gid = row.getFields().get("gid");
						assertNotNull(gid);
						// original name check
						assertEquals(gid.getName(), "gid");
						Field tid = row.getFields().get("tid");
						assertNotNull(tid);
						// original name check
						assertEquals(tid.getName(), "tid");
						Field kname = row.getFields().get("kname");
						assertNotNull(kname);
						// original name check
						assertEquals(kname.getName(), "kname");
						// Create operation
						rowset.insertAt(row, true, 1, fields.find("gname"), "-g- must be overwritten", fields.find("tname"), "-t- must be overwritten", fields.find("kname"), "-k- must be overwritten");
						assertNotNull(row.getColumnObject(fields.find("gid")));
						assertNotNull(row.getColumnObject(fields.find("tid")));
						assertNotNull(row.getColumnObject(fields.find("kid")));
						// Update operation
						row.setColumnObject(fields.find("gid"), NEW_RECORD_ID);
						// initialization was performed for "tid" field
						row.setColumnObject(fields.find("kid"), NEW_RECORD_ID);
						assertEquals(row.getColumnObject(fields.find("gid")), NEW_RECORD_ID);
						assertEquals(row.getColumnObject(fields.find("tid")), NEW_RECORD_ID);
						assertEquals(row.getColumnObject(fields.find("kid")), NEW_RECORD_ID);
						//
						client.getAppQuery(COMMAND_QUERY_ID, new QueryCallbackAdapter() {
							@Override
							public void run(Query aQuery) throws Exception {
								Query command = aQuery;
								command.putParameter("gid", DataTypeInfo.DECIMAL, NEW_RECORD_ID);
								command.putParameter("gname", DataTypeInfo.VARCHAR, NEW_RECORD_NAME_G);
								Change enqueued = command.enqueueUpdate();
								commonLog.add(enqueued);
								// rowset.updateObject(fiedls.find("gname"),
								// NEW_RECORD_NAME_G);
								rowset.updateObject(fields.find("tname"), NEW_RECORD_NAME_T);
								rowset.updateObject(fields.find("kname"), NEW_RECORD_NAME_K);
								client.commit(commonLog, new CancellableCallback() {

									@Override
									public void cancel() {
									}

									@Override
									public void run() throws Exception {
										assertTrue(commonLog.isEmpty());
										rowset.refresh(new CancellableCallback() {
											@Override
											public void cancel() {
											}

											@Override
											public void run() throws Exception {
												final Fields fields = rowset.getFields();
												assertEquals(oldRowsetSize + 1, rowset.size());

												Row newRow = null;
												rowset.beforeFirst();
												while (rowset.next()) {
													if (NEW_RECORD_ID.intValue() == rowset.getInt(fields.find("gid"))) {
														newRow = rowset.getCurrentRow();
														break;
													}
												}
												assertNotNull(newRow);
												assertEquals(newRow.getColumnObject(fields.find("gid")), NEW_RECORD_ID);
												assertEquals(newRow.getColumnObject(fields.find("tid")), NEW_RECORD_ID);
												assertEquals(newRow.getColumnObject(fields.find("kid")), NEW_RECORD_ID);
												assertEquals(newRow.getColumnObject(fields.find("gname")), NEW_RECORD_NAME_G);
												assertEquals(newRow.getColumnObject(fields.find("tname")), NEW_RECORD_NAME_T);
												assertEquals(newRow.getColumnObject(fields.find("kname")), NEW_RECORD_NAME_K);
												// Delete operation
												rowset.delete();
												client.commit(commonLog, new CancellableCallback() {
													@Override
													public void cancel() {
													}

													@Override
													public void run() throws Exception {
														rowset.refresh(new CancellableCallback() {

															@Override
															public void cancel() {
															}

															@Override
															public void run() throws Exception {
																Fields fields = rowset.getFields();
																assertEquals(oldRowsetSize, rowset.size());

																Row newRow = null;
																rowset.beforeFirst();
																while (rowset.next()) {
																	if (NEW_RECORD_ID.intValue() == rowset.getInt(fields.find("gid"))) {
																		newRow = rowset.getCurrentRow();
																		break;
																	}
																}
																assertNull(newRow);
																// tell the test
																// system the
																// test is now
																// done
																finishTest();
															}

														}, new StringCallbackAdapter() {
															@Override
															protected void doWork(String aResult) throws Exception {
															}
														});
													}
												}, null);
											}
										}, new StringCallbackAdapter() {
											@Override
											protected void doWork(String aResult) throws Exception {
											}
										});
									}

								}, null);
							}
						}, new StringCallbackAdapter() {
							@Override
							protected void doWork(String aResult) throws Exception {
							}
						});
					}

				}, new StringCallbackAdapter() {
					@Override
					protected void doWork(String aResult) throws Exception {
					}
				});
			}
		}, new StringCallbackAdapter() {
			@Override
			protected void doWork(String aResult) throws Exception {
			}
		});
	}
}
