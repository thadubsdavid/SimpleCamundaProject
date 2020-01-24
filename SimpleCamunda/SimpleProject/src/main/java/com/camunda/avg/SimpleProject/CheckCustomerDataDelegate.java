package com.camunda.avg.SimpleProject;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.connect.Connectors;
import org.camunda.connect.httpclient.HttpConnector;
import org.camunda.connect.httpclient.HttpResponse;
import org.camunda.spin.json.SpinJsonNode;
import static org.camunda.spin.Spin.*;

public class CheckCustomerDataDelegate implements JavaDelegate {

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		HttpConnector http = Connectors.getConnector(HttpConnector.ID);
        
		//id des Kunden für URL
		String KundenId = (String) execution.getVariable("KundenId");
		
        HttpResponse response = http.createRequest()
        		.get()
        		.header("Accept", "application/json")
        		.url("http://localhost:8091/customers/"+ KundenId)
        		.execute();
        
        
        Integer statusCode = response.getStatusCode();
        String contentTypeHeader = response.getHeader("Content-Type");
        String body = response.getResponse();
        
        System.out.println("statusCode: " + statusCode);
        System.out.println("contentTypeHeader: " + contentTypeHeader);
        System.out.println("body: " + body);
        
        SpinJsonNode customerData = JSON(body);
        
        SpinJsonNode customerPropertyCustomerId = customerData.prop("id");
        Long id = (Long) customerPropertyCustomerId.numberValue();
        
        SpinJsonNode customerPropertyReportedDamages = customerData.prop("reportedDamages");
        Long reportedDamages = (Long) customerPropertyReportedDamages.numberValue();
        
        SpinJsonNode customerPropertyMonthlyNetIncome = customerData.prop("monthlyNetIncome");
        Long monthlyNetIncome = (Long) customerPropertyMonthlyNetIncome.numberValue();
        
        SpinJsonNode customerPropertyLoyalityScore = customerData.prop("loyalityScore");
        Long loyalityScore = (Long) customerPropertyLoyalityScore.numberValue();
        
        SpinJsonNode customerPropertyRiskScore = customerData.prop("riskScore");
        Long riskScore = (Long) customerPropertyRiskScore.numberValue();
                
        //TODO: evtl. Konvertieren zu Kundendaten Klasse
        //			-> body parsen und Werte übernehmen sonst muss an anderer Stelle jedes mal geparst werden
        //		Requests überprüfen
        
        execution.setVariable(execution.getId(), "CustomerId", id.toString());
        execution.setVariable(execution.getId(), "CustomerReportedDamages", reportedDamages.toString());
        execution.setVariable(execution.getId(), "CustomerMonthlyNetIncome", monthlyNetIncome.toString());
        execution.setVariable(execution.getId(), "CustomerLoyalityScore", loyalityScore.toString());
        execution.setVariable(execution.getId(), "CustomerRiskScore", riskScore.toString());
        
        response.close();
	}

}