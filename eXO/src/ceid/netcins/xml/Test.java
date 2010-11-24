/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ceid.netcins.xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author andy
 */

@XmlRootElement( name="test" )
public class Test {
    
    @XmlElement
    public List<Scenario> scenario;
    
    public Test () {
        
    }
    
    public Test (List<Scenario> scenarios) {
        this.scenario = scenarios;
    }
    
    public static Test load(File xmlFile) {
        
        Test testInstance = null;
        
        try {           
            JAXBContext jc = JAXBContext.newInstance(Test.class);
            Unmarshaller u = jc.createUnmarshaller();
            testInstance = (Test) u.unmarshal(new FileInputStream(xmlFile));
        } catch (FileNotFoundException ex) {
            // TODO : Logging
            System.out.println(ex.getMessage());
        } catch (JAXBException ex) {
            // TODO : Logging
            System.out.println(ex.getMessage());
        }
        
        return testInstance;
    }
    
}
