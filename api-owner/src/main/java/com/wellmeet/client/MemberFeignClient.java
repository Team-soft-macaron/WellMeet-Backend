package com.wellmeet.client;

import com.wellmeet.client.dto.MemberDTO;
import com.wellmeet.client.dto.request.MemberIdsRequest;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "domain-member-service")
public interface MemberFeignClient {

    @GetMapping("/api/members/{id}")
    MemberDTO getMember(@PathVariable("id") String id);

    @PostMapping("/api/members/batch")
    List<MemberDTO> getMembersByIds(@RequestBody MemberIdsRequest request);
}
