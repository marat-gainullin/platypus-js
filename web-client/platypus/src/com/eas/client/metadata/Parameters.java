/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.metadata;

/**
 * <code>Parameters</code> class is intended to hold and process information
 * about parameters of forms, queries, reports and data modules.
 */
public class Parameters extends Fields {

	private static final String DEFAULT_PARAM_NAME_PREFIX = "Param";

	/**
	 * The default constructor.
	 */
	public Parameters() {
		super();
	}

	/**
	 * The copy constructor.
	 * 
	 * @param aSource
	 */
	public Parameters(Parameters aSource) {
		super(aSource);
	}

	/**
	 * Returns default name prefix for generating new parameter names.
	 * 
	 * @return New (unique) name for this instance of <code>Parameters</code>.
	 * @see #generateNewName()
	 * @see #generateNewName(String)
	 */
	@Override
	public String getDefaultNamePrefix() {
		return DEFAULT_PARAM_NAME_PREFIX;
	}

	/**
	 * Creates new parameter
	 * 
	 * @return New <code>Parameter</code> instance.
	 */
	@Override
	public Parameter createNewField() {
		return (Parameter) super.createNewField();
	}

	/**
	 * Creates new parameter with the specified name.
	 * 
	 * @return New <code>Parameter</code> instance.
	 * @see #createNewField()
	 * @see #getDefaultNamePrefix()
	 * @see #generateNewName()
	 * @see #generateNewName(String)
	 * @see #isNameAlreadyPresent(String aName, Field aField2Skip)
	 */
	@Override
	public Parameter createNewField(String aName) {
		if (aName == null || aName.isEmpty())
			aName = generateNewName();
		Parameter param = new Parameter(aName, null);
		param.setType("String");
		return param;
	}

	/**
	 * Returns <code>Parameter</code> instance at the specified index. Index is
	 * 1 based.
	 * 
	 * @param index
	 *            Index of <code>Parameter</code> instance you are interested
	 *            in.
	 * @return <code>Parameter</code> instance at the specified index. If wrong
	 *         index specified, than null is returned.
	 */
	@Override
	public Parameter get(int index) {
		return (Parameter) super.get(index);
	}

	/**
	 * Returns <code>Parameter</code> instance with the specified name.
	 * 
	 * @param aParameterName
	 *            Parameter name of <code>Parameter</code> instance you are
	 *            interested in.
	 * @return <code>Parameter</code> instance with the specified name. If
	 *         absent name is specified, than null is returned.
	 */
	@Override
	public Parameter get(String aParameterName) {
		return (Parameter) super.get(aParameterName);
	}

	/**
	 * Returns parameters count, contained in this parameters set. Same as
	 * <code>Fields.getFieldsCount()</code>.
	 * 
	 * @return Parameters count, contained in this parameters set.
	 * @see #getFieldsCount()
	 */
	public int getParametersCount() {
		return getFieldsCount();
	}

	/**
	 * Copies this <code>Parameters</code> instance, creating a new one.
	 * 
	 * @return New instance of <code>Parameters</code>.
	 * @see #clone()
	 */
	@Override
	public Parameters copy() {
		return new Parameters(this);
	}
}
