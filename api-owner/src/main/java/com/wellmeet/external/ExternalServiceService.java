package com.wellmeet.external;

import com.wellmeet.auth.dto.MessageResponse;
import com.wellmeet.external.dto.IntegrationStatusResponse;
import com.wellmeet.external.dto.IntegrationConfigRequest;
import com.wellmeet.external.dto.IntegrationTestResponse;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
public class ExternalServiceService {

    public IntegrationStatusResponse getIntegrationStatus(Long ownerId) {
        // TODO: 실제 연동 상태 조회 로직 구현
        IntegrationStatusResponse.ServiceStatus posStatus = 
            new IntegrationStatusResponse.ServiceStatus(false, null, null, null, null);
        
        IntegrationStatusResponse.ServiceStatus paymentStatus = 
            new IntegrationStatusResponse.ServiceStatus(true, "토스페이먼츠", 
                LocalDateTime.now().toString(), Arrays.asList("카드", "계좌이체"), null);
        
        IntegrationStatusResponse.ServiceStatus deliveryStatus = 
            new IntegrationStatusResponse.ServiceStatus(true, "배달의민족", 
                LocalDateTime.now().toString(), null, Arrays.asList("배달의민족", "요기요"));
        
        IntegrationStatusResponse.ServiceStatus marketingStatus = 
            new IntegrationStatusResponse.ServiceStatus(false, null, null, null, null);
        
        IntegrationStatusResponse.ServiceStatus accountingStatus = 
            new IntegrationStatusResponse.ServiceStatus(false, null, null, null, null);
        
        IntegrationStatusResponse.Integrations integrations = 
            new IntegrationStatusResponse.Integrations(posStatus, paymentStatus, 
                deliveryStatus, marketingStatus, accountingStatus);
        
        return new IntegrationStatusResponse(integrations);
    }

    public MessageResponse connectService(Long ownerId, String service, IntegrationConfigRequest request) {
        // TODO: 실제 서비스 연동 로직 구현
        return new MessageResponse(service + " 서비스가 성공적으로 연결되었습니다.");
    }

    public void disconnectService(Long ownerId, String service) {
        // TODO: 실제 서비스 연결 해제 로직 구현
    }

    public IntegrationTestResponse testConnection(Long ownerId, String service) {
        // TODO: 실제 연결 테스트 로직 구현
        IntegrationTestResponse.TestDetails details = 
            new IntegrationTestResponse.TestDetails(150L, "v2.0", 
                Arrays.asList("결제", "환불", "정산"));
        
        return new IntegrationTestResponse(true, "연결 테스트 성공", details);
    }
}