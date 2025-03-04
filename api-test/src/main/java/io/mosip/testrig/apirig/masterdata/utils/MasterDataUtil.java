package io.mosip.testrig.apirig.masterdata.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

import javax.ws.rs.core.MediaType;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.SkipException;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import io.mosip.testrig.apirig.dto.TestCaseDTO;
import io.mosip.testrig.apirig.utils.AdminTestUtil;
import io.mosip.testrig.apirig.utils.ConfigManager;
import io.mosip.testrig.apirig.utils.GlobalConstants;
import io.mosip.testrig.apirig.utils.RestClient;
import io.mosip.testrig.apirig.utils.SkipTestCaseHandler;
import io.restassured.response.Response;

public class MasterDataUtil extends AdminTestUtil {

	private static final Logger logger = Logger.getLogger(MasterDataUtil.class);
	public static DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
	public static String infantDob = LocalDateTime.now().minusYears(getInfantMaxAge()).format(dateFormatter);
	
	public static void setLogLevel() {
		if (MasterDataConfigManager.IsDebugEnabled())
			logger.setLevel(Level.ALL);
		else
			logger.setLevel(Level.ERROR);
	}

	public static String isTestCaseValidForExecution(TestCaseDTO testCaseDTO) {
		String testCaseName = testCaseDTO.getTestCaseName();

		if (SkipTestCaseHandler.isTestCaseInSkippedList(testCaseName)) {
			throw new SkipException(GlobalConstants.KNOWN_ISSUES);
		}
		return testCaseName;
	}

	public String inputJsonStringKeyWordHandeler(String jsonString, String testCaseName) {

		if (jsonString.contains("$INFANT$")) {
			jsonString = replaceKeywordWithValue(jsonString, "$INFANT$", infantDob);
		}

		return jsonString;
	}

	private static int getInfantMaxAge() {
		HashMap<String, String> map = null;
		String ageGroup = getValueFromRegprocActuator("systemProperties",
				"mosip.regproc.packet.classifier.tagging.agegroup.ranges");

		try {
			map = new Gson().fromJson(ageGroup, new TypeToken<HashMap<String, String>>() {
			}.getType());
		} catch (Exception e) {
			logger.error(
					GlobalConstants.ERROR_STRING_1 + ageGroup + GlobalConstants.EXCEPTION_STRING_1 + e.getMessage());
		}

		String infantAgeGroup = map.get("INFANT").toString();

		String[] parts = infantAgeGroup.split("-");
		return Integer.parseInt(parts[1]);
	}
	
	public static JSONArray regprocActuatorResponseArray = null;

	public static String getValueFromRegprocActuator(String section, String key) {
		String url = ApplnURI + ConfigManager.getproperty("actuatorRegprocEndpoint");
		String actuatorCacheKey = url + section + key;
		String value = actuatorValueCache.get(actuatorCacheKey);
		if (value != null && !value.isEmpty())
			return value;

		try {
			if (regprocActuatorResponseArray == null) {
				Response response = null;
				JSONObject responseJson = null;
				response = RestClient.getRequest(url, MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON);

				responseJson = new JSONObject(response.getBody().asString());
				regprocActuatorResponseArray = responseJson.getJSONArray("propertySources");
			}
			for (int i = 0, size = regprocActuatorResponseArray.length(); i < size; i++) {
				JSONObject eachJson = regprocActuatorResponseArray.getJSONObject(i);
				if (eachJson.get("name").toString().contains(section)) {
					value = eachJson.getJSONObject(GlobalConstants.PROPERTIES).getJSONObject(key)
							.get(GlobalConstants.VALUE).toString();
					if (ConfigManager.IsDebugEnabled())
						logger.info("Actuator: " + url + " key: " + key + " value: " + value);
					break;
				}
			}
			actuatorValueCache.put(actuatorCacheKey, value);

			return value;
		} catch (Exception e) {
			logger.error(GlobalConstants.EXCEPTION_STRING_2 + e);
			return "";
		}

	}

}