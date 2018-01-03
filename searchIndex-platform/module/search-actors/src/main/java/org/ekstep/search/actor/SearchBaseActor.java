package org.ekstep.search.actor;

import java.util.Map;
import java.util.Map.Entry;

import org.ekstep.common.dto.Request;
import org.ekstep.common.dto.Response;
import org.ekstep.common.dto.ResponseParams;
import org.ekstep.common.dto.ResponseParams.StatusType;
import org.ekstep.common.exception.ClientException;
import org.ekstep.common.exception.MiddlewareException;
import org.ekstep.common.exception.ResourceNotFoundException;
import org.ekstep.common.exception.ResponseCode;
import org.ekstep.common.exception.ServerException;
import org.ekstep.graph.common.exception.GraphEngineErrorCodes;
import org.ekstep.telemetry.logger.TelemetryManager;
import org.ekstep.common.dto.CoverageIgnore;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;

public abstract class SearchBaseActor extends UntypedActor {

    
    private static final String ekstep = "org.ekstep.";
    private static final String ilimi = "org.ekstep.";
    private static final String java = "java.";
    private static final String default_err_msg = "Something went wrong in server while processing the request";
    
    @CoverageIgnore
    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof Request) {
            Request request = (Request) message;
            invokeMethod(request, getSender());
        } else if (message instanceof Response) {
            // do nothing
        } else {
            unhandled(message);
        }
    }

    protected abstract void invokeMethod(Request request, ActorRef parent);

    public void OK(String responseIdentifier, Object vo, ActorRef parent) {
        Response response = new Response();
        response.put(responseIdentifier, vo);
        response.setParams(getSucessStatus());
        parent.tell(response, getSelf());
    }

    @CoverageIgnore
    public void OK(Map<String, Object> responseObjects, ActorRef parent) {
        Response response = new Response();
        if (null != responseObjects && responseObjects.size() > 0) {
            for (Entry<String, Object> entry : responseObjects.entrySet()) {
                response.put(entry.getKey(), entry.getValue());
            }
        }
        response.setParams(getSucessStatus());
        parent.tell(response, getSelf());
    }

    @CoverageIgnore
    public void ERROR(String errorCode, String errorMessage, ResponseCode code, String responseIdentifier, Object vo, ActorRef parent) {
        TelemetryManager.log("Error", errorCode , errorMessage);
        Response response = new Response();
        response.put(responseIdentifier, vo);
        response.setParams(getErrorStatus(errorCode, errorMessage));
        response.setResponseCode(code);
        parent.tell(response, getSelf());
    }

    @CoverageIgnore
    public void handleException(Throwable e, ActorRef parent) {
        TelemetryManager.log("Error", e.getMessage());
        Response response = new Response();
        ResponseParams params = new ResponseParams();
        params.setStatus(StatusType.failed.name());
        if (e instanceof MiddlewareException) {
            MiddlewareException mwException = (MiddlewareException) e;
            params.setErr(mwException.getErrCode());
        } else {
            params.setErr(GraphEngineErrorCodes.ERR_SYSTEM_EXCEPTION.name());
        }
        TelemetryManager.log("Exception occured in class :"+ e.getClass().getName() , e.getMessage());
        params.setErrmsg(setErrMessage(e));
        response.setParams(params);
        setResponseCode(response, e);
        parent.tell(response, getSelf());
    }

    private ResponseParams getSucessStatus() {
        ResponseParams params = new ResponseParams();
        params.setErr("0");
        params.setStatus(StatusType.successful.name());
        params.setErrmsg("Operation successful");
        return params;
    }

    @CoverageIgnore
    private ResponseParams getErrorStatus(String errorCode, String errorMessage) {
        ResponseParams params = new ResponseParams();
        params.setErr(errorCode);
        params.setStatus(StatusType.failed.name());
        params.setErrmsg(errorMessage);
        return params;
    }

    @CoverageIgnore
    private void setResponseCode(Response res, Throwable e) {
        if (e instanceof ClientException) {
            res.setResponseCode(ResponseCode.CLIENT_ERROR);
        } else if (e instanceof ServerException) {
            res.setResponseCode(ResponseCode.SERVER_ERROR);
        } else if (e instanceof ResourceNotFoundException) {
            res.setResponseCode(ResponseCode.RESOURCE_NOT_FOUND);
        } else {
            res.setResponseCode(ResponseCode.SERVER_ERROR);
        }
    }
    
    protected String setErrMessage(Throwable e){
    	Class<? extends Throwable> className = e.getClass();
        if(className.getName().contains(ekstep) || className.getName().contains(ilimi)){
        	TelemetryManager.log("Setting error message sent from class " + className , e.getMessage());
        	return e.getMessage();
        }
        else if(className.getName().startsWith(java)){
        	TelemetryManager.log("Setting default err msg " + className , e.getMessage());
        	return default_err_msg;
        }
        return null;
    }
}
