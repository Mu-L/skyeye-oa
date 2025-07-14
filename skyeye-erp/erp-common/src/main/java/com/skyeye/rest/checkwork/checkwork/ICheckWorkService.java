package com.skyeye.rest.checkwork.checkwork;

import java.util.List;
import java.util.Map;

public interface ICheckWorkService {
    List<Map<String, Object>> queryInfoByStaffIdsAndDates(String staffIds, String dates);
}
