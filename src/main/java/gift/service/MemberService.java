package gift.service;

import gift.dto.KakaoUserInfoResponseDto;
import gift.dto.MemberRequestDto;
import gift.dto.MemberResponseDto;
import gift.entity.Member;
import gift.exception.EmailAlreadyExistsException;
import gift.exception.InvalidCredentialsException;
import gift.exception.ResourceNotFoundException;
import gift.repository.MemberRepository;
import gift.security.JwtTokenProvider;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MemberService {
    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public MemberService(MemberRepository memberRepository, JwtTokenProvider jwtTokenProvider) {
        this.memberRepository = memberRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Transactional
    public MemberResponseDto registerMember(MemberRequestDto memberRequestDto) {
        Optional<Member> optionalMember = memberRepository.findByEmail(memberRequestDto.email());

        if (optionalMember.isPresent()) {
            throw new EmailAlreadyExistsException("이미 등록된 이메일입니다.");
        }

        Member member = new Member(memberRequestDto.email(), memberRequestDto.password(), "LOCAL");
        memberRepository.save(member);

        return new MemberResponseDto(jwtTokenProvider.generateToken(member));
    }

    @Transactional(readOnly = true)
    public MemberResponseDto loginMember(MemberRequestDto memberRequestDto) {
        Member member = memberRepository.findByEmail(memberRequestDto.email())
                .orElseThrow(() -> new ResourceNotFoundException("등록된 사용자가 아닙니다."));

        if (!"LOCAL".equals(member.getProvider())) {
            throw new InvalidCredentialsException("카카오로 가입된 계정입니다. 카카오 로그인을 이용해주세요.");
        }

        if (!member.getPassword().equals(memberRequestDto.password())) {
            throw new InvalidCredentialsException("비밀번호가 일치하지 않습니다.");
        }

        return new MemberResponseDto(jwtTokenProvider.generateToken(member));
    }

    @Transactional
    public MemberResponseDto processKakaoLogin(KakaoUserInfoResponseDto kakaoUserInfoResponseDto, String kakaoAccessToken) {
        String email = kakaoUserInfoResponseDto.getEmail();

        Member member = memberRepository.findByEmail(email)
                .orElseGet(() -> {
                    String randomPassword = UUID.randomUUID().toString();
                    Member newMember = new Member(email, randomPassword, "KAKAO");
                    return memberRepository.save(newMember);
                });

        member.setKakaoAccessToken(kakaoAccessToken);

        return new MemberResponseDto(jwtTokenProvider.generateToken(member));
    }
}
