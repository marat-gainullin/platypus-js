/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.reports;

import java.util.Map;
import net.sf.jxls.processor.RowProcessor;
import net.sf.jxls.transformer.Row;
import net.sf.jxls.transformer.RowCollection;
import org.apache.commons.beanutils.BasicDynaBean;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Workbook;

/**
 *
 * @author AB
 */
public class ExcelRowProcessor implements RowProcessor {

    private static final String TEMPLATE_CELL = "cellTemplateLabel";

    @Override
    public void processRow(Row row, Map namedCells) {
        // check if processed row has a parent row
        if (row.getParentRow() != null) {
            // Processed row has parent row. It means we are processing some collection item
            int lenCollections = row.getParentRow().getRowCollections().size();
            for (int i = 0; i < lenCollections; i++) {
                RowCollection rowCollection = (RowCollection) row.getParentRow().getRowCollections().get(i);
                String cellTemplateName = getCellTemplate(rowCollection.getIterateObject());
                if (namedCells.containsKey(cellTemplateName)) {
                    net.sf.jxls.parser.Cell customCell = (net.sf.jxls.parser.Cell) namedCells.get(cellTemplateName);
                    for (int j = 0; j < row.getCells().size(); j++) {
                        net.sf.jxls.parser.Cell cell = (net.sf.jxls.parser.Cell) row.getCells().get(j);
                        Cell hssfCell = cell.getPoiCell();
                        if (hssfCell != null) {
                            copyStyle(row.getSheet().getPoiWorkbook(), customCell.getPoiCell(), hssfCell);
                        }
                    }
                }
            }
        }
    }

    public String getCellTemplate(Object aCollectionItem) {
        if (aCollectionItem != null) {
            if (aCollectionItem instanceof BasicDynaBean) {
                Object temp = null;
                try {
                    temp = ((BasicDynaBean) aCollectionItem).get(TEMPLATE_CELL);
                } catch (IllegalArgumentException e) {
                }
                if (temp != null) {
                    return (String) temp;
                }
            }
        }
        return "";
    }

    private void copyStyle(Workbook workbook, Cell fromCell, Cell toCell) {
        CellStyle toStyle = toCell.getCellStyle();
        CellStyle fromStyle = fromCell.getCellStyle();
        CellStyle newStyle = workbook.createCellStyle();

        newStyle.setAlignment(fromStyle.getAlignment());
        newStyle.setBorderBottom(fromStyle.getBorderBottom());
        newStyle.setBorderLeft(fromStyle.getBorderLeft());
        newStyle.setBorderRight(fromStyle.getBorderRight());
        newStyle.setBorderTop(fromStyle.getBorderTop());
        newStyle.setBottomBorderColor(fromStyle.getBottomBorderColor());

        newStyle.setFillBackgroundColor(fromStyle.getFillBackgroundColor());
        newStyle.setFillForegroundColor(fromStyle.getFillForegroundColor());
        newStyle.setFillPattern(fromStyle.getFillPattern());
        newStyle.setFont(workbook.getFontAt(fromStyle.getFontIndex()));
        newStyle.setHidden(fromStyle.getHidden());
        newStyle.setIndention(fromStyle.getIndention());
        newStyle.setLeftBorderColor(fromStyle.getLeftBorderColor());
        newStyle.setLocked(fromStyle.getLocked());
        newStyle.setRightBorderColor(fromStyle.getRightBorderColor());
        newStyle.setTopBorderColor(fromStyle.getTopBorderColor());
        newStyle.setVerticalAlignment(fromStyle.getVerticalAlignment());
        newStyle.setWrapText(fromStyle.getWrapText());
        newStyle.setDataFormat(toStyle.getDataFormat());
        toCell.setCellStyle(newStyle);
    }
}
