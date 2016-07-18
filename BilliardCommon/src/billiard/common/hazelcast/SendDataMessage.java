/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package billiard.common.hazelcast;

import billiard.common.PermittedValues;
import java.io.Serializable;

/**
 *
 * @author jean
 */
public class SendDataMessage implements Serializable {
    private static final long serialVersionUID = 1L;
    private PermittedValues.ActionObject actionObject;
    private Object dataObject;

    public SendDataMessage(PermittedValues.ActionObject actionObject, Object dataObject) {
        this.actionObject = actionObject;
        this.dataObject = dataObject;
    }

    public PermittedValues.ActionObject getActionObject() {
        return actionObject;
    }

    public void setActionObject(PermittedValues.ActionObject actionObject) {
        this.actionObject = actionObject;
    }

    public Object getDataObject() {
        return dataObject;
    }

    public void setDataObject(Object dataObject) {
        this.dataObject = dataObject;
    }
}
