package com.wellmeet.batch.client;

import com.wellmeet.common.dto.MemberDTO;
import com.wellmeet.common.dto.request.MemberIdsRequest;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "domain-member-service", path = "/api/members")
public interface MemberClient {

    @GetMapping("/{id}")
    MemberDTO getMemberById(@PathVariable("id") String id);

    @PostMapping("/batch")
    List<MemberDTO> getMembersByIds(@RequestBody MemberIdsRequest request);
}
