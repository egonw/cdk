package org.openscience.cdk.atomtype;

import java.util.Map;

import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IBond.Order;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IChemObjectChangeEvent;
import org.openscience.cdk.interfaces.IChemObjectListener;

public enum CDKAtomType implements IAtomType {
	Csp2(Double.parseDouble("0.0"));
	
	private final Double naturalAbundance;
	
	CDKAtomType(Double naturalAbundance) {
		this.naturalAbundance = naturalAbundance;
	}

	@Override
	public void setNaturalAbundance(Double naturalAbundance) {
		throw new UnsupportedOperationException("Cannot set properties of CDKAtomType enums");		
	}

	@Override
	public void setExactMass(Double exactMass) {
		throw new UnsupportedOperationException("Cannot set properties of CDKAtomType enums");
	}

	@Override
	public Double getNaturalAbundance() {
		return this.naturalAbundance;
	}

	@Override
	public Double getExactMass() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer getMassNumber() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setMassNumber(Integer massNumber) {
		throw new UnsupportedOperationException("Cannot set properties of CDKAtomType enums");
	}

	@Override
	public Integer getAtomicNumber() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setAtomicNumber(Integer atomicNumber) {
		throw new UnsupportedOperationException("Cannot set properties of CDKAtomType enums");
	}

	@Override
	public String getSymbol() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setSymbol(String symbol) {
		throw new UnsupportedOperationException("Cannot set properties of CDKAtomType enums");
	}

	@Override
	public void addListener(IChemObjectListener col) {
		throw new UnsupportedOperationException("Cannot set properties of CDKAtomType enums");
	}

	@Override
	public int getListenerCount() {
		throw new UnsupportedOperationException("Cannot set properties of CDKAtomType enums");
	}

	@Override
	public void removeListener(IChemObjectListener col) {
		throw new UnsupportedOperationException("Cannot set properties of CDKAtomType enums");
	}

	@Override
	public void setNotification(boolean bool) {
		throw new UnsupportedOperationException("Cannot set properties of CDKAtomType enums");		
	}

	@Override
	public boolean getNotification() {
		throw new UnsupportedOperationException("Cannot set properties of CDKAtomType enums");
	}

	@Override
	public void notifyChanged() {
		throw new UnsupportedOperationException("Cannot set properties of CDKAtomType enums");		
	}

	@Override
	public void notifyChanged(IChemObjectChangeEvent evt) {
		throw new UnsupportedOperationException("Cannot set properties of CDKAtomType enums");
	}

	@Override
	public void setProperty(Object description, Object property) {
		throw new UnsupportedOperationException("Cannot set properties of CDKAtomType enums");
	}

	@Override
	public void removeProperty(Object description) {
		throw new UnsupportedOperationException("Cannot set properties of CDKAtomType enums");
	}

	@Override
	public <T> T getProperty(Object description) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T getProperty(Object description, Class<T> c) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<Object, Object> getProperties() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getID() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setID(String identifier) {
		throw new UnsupportedOperationException("Cannot set properties of CDKAtomType enums");
	}

	@Override
	public void setFlag(int mask, boolean value) {
		throw new UnsupportedOperationException("Cannot set properties of CDKAtomType enums");
	}

	@Override
	public boolean getFlag(int mask) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setProperties(Map<Object, Object> properties) {
		throw new UnsupportedOperationException("Cannot set properties of CDKAtomType enums");
	}

	@Override
	public void addProperties(Map<Object, Object> properties) {
		throw new UnsupportedOperationException("Cannot set properties of CDKAtomType enums");
	}

	@Override
	public void setFlags(boolean[] newFlags) {
		throw new UnsupportedOperationException("Cannot set properties of CDKAtomType enums");
	}

	@Override
	public boolean[] getFlags() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Number getFlagValue() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void set(int flags) {
		throw new UnsupportedOperationException("Cannot set properties of CDKAtomType enums");
	}

	@Override
	public void clear(int flags) {
		throw new UnsupportedOperationException("Cannot set properties of CDKAtomType enums");
	}

	@Override
	public boolean is(int flags) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int flags() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public IChemObjectBuilder getBuilder() {
		throw new UnsupportedOperationException("Cannot set properties of CDKAtomType enums");
	}

	@Override
	public void setAtomTypeName(String identifier) {
		throw new UnsupportedOperationException("Cannot set properties of CDKAtomType enums");
	}

	@Override
	public void setBondOrderSum(Double bondOrderSum) {
		throw new UnsupportedOperationException("Cannot set properties of CDKAtomType enums");
	}

	@Override
	public String getAtomTypeName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Order getMaxBondOrder() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Double getBondOrderSum() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setFormalCharge(Integer charge) {
		throw new UnsupportedOperationException("Cannot set properties of CDKAtomType enums");
	}

	@Override
	public Integer getFormalCharge() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setFormalNeighbourCount(Integer count) {
		throw new UnsupportedOperationException("Cannot set properties of CDKAtomType enums");
	}

	@Override
	public Integer getFormalNeighbourCount() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setHybridization(Hybridization hybridization) {
		throw new UnsupportedOperationException("Cannot set properties of CDKAtomType enums");
	}

	@Override
	public Hybridization getHybridization() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setCovalentRadius(Double radius) {
		throw new UnsupportedOperationException("Cannot set properties of CDKAtomType enums");
	}

	@Override
	public Double getCovalentRadius() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setValency(Integer valency) {
		throw new UnsupportedOperationException("Cannot set properties of CDKAtomType enums");
	}

	@Override
	public Integer getValency() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setMaxBondOrder(Order arg0) {
		throw new UnsupportedOperationException("Cannot set properties of CDKAtomType enums");
	}


}
