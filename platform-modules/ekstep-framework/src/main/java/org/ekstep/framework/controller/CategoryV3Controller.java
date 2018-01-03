package org.ekstep.framework.controller;

import java.util.Map;

import org.ekstep.common.dto.Request;
import org.ekstep.common.dto.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import org.ekstep.common.controller.BaseController;
import org.ekstep.framework.mgr.ICategoryManager;
import org.ekstep.telemetry.logger.TelemetryManager;

/**
 * This is the entry point for all CRUD operations related to category API.
 * 
 * @author Rashmi
 *
 */
@Controller
@RequestMapping("/v3/category")
public class CategoryV3Controller extends BaseController {

@Autowired
private ICategoryManager categoryManager;
	
	/**
	 * 
	 * @param requestMap
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/create", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<Response> create(@RequestBody Map<String, Object> requestMap) {
		String apiId = "ekstep.learning.category.create";
		try {
			Request request = getRequest(requestMap);
			Map<String, Object> map = (Map<String, Object>) request.get("category");
			Response response = categoryManager.createCategory(map);
			return getResponseEntity(response, apiId, null);
		} catch (Exception e) {
			TelemetryManager.log("Create category", e.getMessage(), e);
			return getExceptionResponseEntity(e, apiId, null);
		}
	}
	
	/**
	 * 
	 * @param categoryId
	 * @return
	 */
	@RequestMapping(value = "/read/{id:.+}", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<Response> read(@PathVariable(value = "id") String categoryId) {
		String apiId = "ekstep.learning.category.read";
		try {
			Response response = categoryManager.readCategory(categoryId);
			return getResponseEntity(response, apiId, null);
		} catch (Exception e) {
			TelemetryManager.log("Read category", e.getMessage(), e);
			return getExceptionResponseEntity(e, apiId, null);
		}
	}
	
	/**
	 * 
	 * @param categoryId
	 * @param requestMap
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/update/{id:.+}", method = RequestMethod.PATCH)
	@ResponseBody
	public ResponseEntity<Response> update(@PathVariable(value = "id") String categoryId,
			@RequestBody Map<String, Object> requestMap) {
		String apiId = "ekstep.learning.category.update";
		try {
			Request request = getRequest(requestMap);
			Map<String, Object> map = (Map<String, Object>) request.get("category");
			Response response = categoryManager.updateCategory(categoryId, map);
			return getResponseEntity(response, apiId, null);
		} catch (Exception e) {
			TelemetryManager.log("Update category", e.getMessage(), e);
			return getExceptionResponseEntity(e, apiId, null);
		}
	}
	
	/**
	 * 
	 * @param map
	 * @param userId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/search", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<Response> search(@RequestBody Map<String, Object> requestMap) {
		String apiId = "ekstep.learning.category.search";
		try {
			Request request = getRequest(requestMap);
			Map<String, Object> map = (Map<String, Object>) request.get("search");
			Response response = categoryManager.searchCategory(map);
			TelemetryManager.log("search category | Response: " + response);
			return getResponseEntity(response, apiId, null);
		} catch (Exception e) {
			TelemetryManager.log("search category | Exception: " , e.getMessage(), e);
			return getExceptionResponseEntity(e, apiId, null);
		}
	}
	
	/**
	 * 
	 * @param categoryId
	 * @return
	 */
	@RequestMapping(value = "/retire/{id:.+}", method = RequestMethod.DELETE)
	@ResponseBody
	public ResponseEntity<Response> retire(@PathVariable(value = "id") String categoryId) {
		String apiId = "ekstep.learning.category.retire";
		try {
			Response response = categoryManager.retireCategory(categoryId);
			TelemetryManager.log("retire category | Response: " + response);
			return getResponseEntity(response, apiId, null);
		} catch (Exception e) {
			TelemetryManager.log("retire category | Exception: " , e.getMessage(), e);
			return getExceptionResponseEntity(e, apiId, null);
		}
	}
}