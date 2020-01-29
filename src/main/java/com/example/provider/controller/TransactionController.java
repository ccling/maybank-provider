package com.example.provider.controller;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.provider.constant.Common;
import com.example.provider.constant.TrxField;
import com.example.provider.model.Transaction;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/trx")
public class TransactionController {
	private static final Logger LOGGER = LoggerFactory.getLogger(TransactionController.class);

//	@Autowired
//	private TransactionService transactionService;
	
	private ObjectMapper mapper = new ObjectMapper();
	
	/*
	 * {"pageNo": 1, "pageSize": 10, "totalPages": 0, "fields": ["ACCOUNT_NUMBER", "TRX_AMOUNT"], "condition": {"CUSTOMER_ID": "222"}}
	 */
	@GetMapping("/get")
	public JSONObject getTransaction(HttpServletRequest request) {
		try {
			JSONObject inputJson = mapper.readValue(request.getInputStream(),JSONObject.class);

			Iterator itr = inputJson.getObject("condition", JSONObject.class).keySet().iterator();
			String key = null;
			while (itr.hasNext()) {
				key = (String) itr.next();
				break;
			}
			
			if (key == null) {
				return buildMsg((byte) -2, "Condition not found!");
			}
			
			Transaction trx = new Transaction();
			switch(key) {
				case TrxField.CUSTOMER_ID:
					trx.setCustId(inputJson.getObject("condition", JSONObject.class).getLong(key));
					break;
				case TrxField.ACCOUNT_NUMBER:
					trx.setAcctNum(inputJson.getObject("condition", JSONObject.class).getLong(key));
					break;
				case TrxField.DESCRIPTION:
					trx.setCustId(Common.trxTypeMap.get(inputJson.getObject("condition", JSONObject.class).getString(key)));
					break;
			}
			
			int totalPages;
			if (inputJson.getInteger("totalPages") == 0) {
			//	int totalRecords = transactionService.getTotal(trx);
			//	totalPage = totalRecords / inputJson.getInteger("pageSize");
				totalPages = 10;
			} else {
				totalPages = inputJson.getInteger("totalPages");
			}
			// List<Transaction> trxList = transactionService.getList(trx, inputJson.getInteger("pageNo") * inputJson.getInteger("pageSize"), inputJson.getInteger("pageSize"));
			List<Transaction> trxList = new ArrayList<Transaction>();
			
			JSONObject retValue = new JSONObject();
			retValue.put("totalPages", totalPages);
			retValue.put("state", (byte)0);
			retValue.put("data", buildTrxArray(trxList, inputJson.getJSONArray("fields")));
			return retValue;
		} catch (Exception e) {
			LOGGER.error("Failed to get transaction, {}", e);
		}
		return buildMsg((byte) -1, "Internal error!");
	}
	
	private JSONObject buildMsg(byte state, String msg) {
		JSONObject retValue = new JSONObject();
		retValue.put("state", state);
		retValue.put("msg", msg);
		return retValue;
	}
	
	private JSONArray buildTrxArray(List<Transaction> trxList, JSONArray fieldsArray) {
		JSONArray trxArray = new JSONArray();
		
		for(Transaction trx : trxList) {
			JSONObject trxObj = new JSONObject();
			
			for (Object obj : fieldsArray) {
				String field = (String) obj;
				
				switch(field) {
					case TrxField.ACCOUNT_NUMBER:
						trxObj.put(field, trx.getAcctNum());
						break;
					case TrxField.TRX_AMOUNT:
						trxObj.put(field, trx.getTrxAmt());
						break;
					case TrxField.DESCRIPTION:
						trxObj.put(field, Common.trxIdMap.get(trx.getTrxTypeId()));
						break;
					case TrxField.TRX_DATE:
						trxObj.put(field, trx.getTrxDate());
						break;
					case TrxField.TRX_TIME:
						trxObj.put(field, trx.getTrxTime());
						break;
					case TrxField.CUSTOMER_ID:
						trxObj.put(field, trx.getCustId());
						break;
				}
			}
			trxArray.add(trxObj);
		}
		return trxArray;
	}
}
