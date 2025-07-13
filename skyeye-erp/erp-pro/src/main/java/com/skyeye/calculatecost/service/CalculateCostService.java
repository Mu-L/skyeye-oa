package com.skyeye.calculatecost.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

public interface CalculateCostService {
    void calculateMachinProcedureAcceptCost(InputObject inputObject, OutputObject outputObject);

    void calculateMachinProcedureCost(InputObject inputObject, OutputObject outputObject);

    void calculateMachinPutCost(InputObject inputObject, OutputObject outputObject);

    void calculateMachinCost(InputObject inputObject, OutputObject outputObject);
}
