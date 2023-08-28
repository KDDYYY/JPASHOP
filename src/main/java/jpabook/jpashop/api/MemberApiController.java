package jpabook.jpashop.api;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;

    @PostMapping("/api/v1/members")
    public CreateMemberResponse saveMemberV1(@RequestBody @Valid Member member){ //jason 데이터를 member로 변환
        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    @PostMapping("/api/v2/members") //회원가입
    public CreateMemberResponse saveMemberV2(@RequestBody @Valid CreateMemberRequest request){ //json 데이터를 member로 변환

       Member member = new Member();
       member.setName(request.getName()); //Entity 값이 바뀌어도 이 부분에서 오류 발생

        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    @PutMapping("/api/v2/members/{id}") //회원 수정
    public UpdateMemberResponse updateMemberV2(
            @PathVariable("id") Long id,
            @RequestBody @Valid UpdateMemberRequest request){
        memberService.update(id, request.getName());
        Member findMember = memberService.findOne(id);
        return new UpdateMemberResponse(findMember.getId(), findMember.getName()); //쿼리를 가져와서 결과 보기 위함
    }

    @GetMapping("/api/v1/members")
    public List<Member> membersV1(){ //Entity 모든 정보들이 출력됨
        return memberService.findMembers();
    }

    @GetMapping("/api/v2/members") //회원 조회
    public Result memberV2(){
        List<Member> findmembers = memberService.findMembers();
        List<MemberDto> collect = findmembers.stream()
                .map(m -> new MemberDto(m.getName())) //Member entity에서 꺼내와 dto에 넣음
                .collect(Collectors.toList());

        return new Result(collect.size(), collect);
    }

    @Data
    @AllArgsConstructor
    static class Result<T>{ //json 배열 타입으로 나가지 않게 하기 위해
        private int count;
        private T data;
    }

    @Data
    @AllArgsConstructor
    static class MemberDto{
        private String name;
    }

    @Data
    static class CreateMemberRequest{  //aPI 스펙에서 name만 들어간것을 알 수 있음
        @NotEmpty
        private String name;
    }
    @Data
    static class CreateMemberResponse{
        private Long id;
        public CreateMemberResponse(Long id) {
            this.id = id;
        }
    }

    @Data
    @AllArgsConstructor
    static class UpdateMemberResponse{
        private Long id;
        private String name;
    }

    @Data
    static class UpdateMemberRequest{
        private String name;
    }
}
