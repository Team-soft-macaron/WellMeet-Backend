package com.wellmeet.domain.member;

import com.wellmeet.common.dto.MemberDTO;
import com.wellmeet.domain.member.dto.CreateMemberRequest;
import com.wellmeet.domain.member.dto.MemberIdsRequest;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class MemberDomainController {

    private final MemberApplicationService memberApplicationService;

    @PostMapping
    public ResponseEntity<MemberDTO> createMember(@Valid @RequestBody CreateMemberRequest request) {
        MemberDTO response = memberApplicationService.createMember(
                request.name(),
                request.nickname(),
                request.email(),
                request.phone()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MemberDTO> getMember(@PathVariable String id) {
        MemberDTO response = memberApplicationService.getMemberById(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/batch")
    public ResponseEntity<List<MemberDTO>> getMembersByIds(
            @Valid @RequestBody MemberIdsRequest request
    ) {
        List<MemberDTO> responses = memberApplicationService.getMembersByIds(request.memberIds());
        return ResponseEntity.ok(responses);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMember(@PathVariable String id) {
        memberApplicationService.deleteMember(id);
        return ResponseEntity.noContent().build();
    }
}
