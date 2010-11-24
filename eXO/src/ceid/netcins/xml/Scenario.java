/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ceid.netcins.xml;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * 
 * @author andy
 */

@XmlType
public class Scenario {

	@XmlElement
	public List<?> index;

	@XmlElement
	public List<?> search;

	@XmlElement
	public List<?> retrieve;

	@XmlElement
	public List<?> tag;

}
