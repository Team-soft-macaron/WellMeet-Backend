package com.wellmeet.domain.member.controller;

import com.wellmeet.domain.member.dto.CreateMemberRequest;
import com.wellmeet.domain.member.dto.MemberIdsRequest;
import com.wellmeet.domain.member.dto.MemberResponse;
import com.wellmeet.domain.member.service.MemberApplicationService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/members")
public class MemberDomainController {

    private final MemberApplicationService memberApplicationService;

    public MemberDomainController(MemberApplicationService memberApplicationService) {
        this.memberApplicationService = memberApplicationService;
    }

    @PostMapping
    public ResponseEntity<MemberResponse> createMember(@Valid @RequestBody CreateMemberRequest request) {
        MemberResponse response = memberApplicationService.createMember(
                request.name(),
                request.nickname(),
                request.email(),
                request.phone()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MemberResponse> getMember(@PathVariable String id) {
        MemberResponse response = memberApplicationService.getMemberById(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/batch")
    public ResponseEntity<List<MemberResponse>> getMembersByIds(
            @Valid @RequestBody MemberIdsRequest request
    ) {
        List<MemberResponse> responses = memberApplicationService.getMembersByIds(request.memberIds());
        return ResponseEntity.ok(responses);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMember(@PathVariable String id) {
        memberApplicationService.deleteMember(id);
        return ResponseEntity.noContent().build();
    }
}
