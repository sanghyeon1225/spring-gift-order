package gift;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import gift.entity.Member;
import gift.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class MemberRepositoryTest {
    @Autowired
    private MemberRepository memberRepository;

    @Test
    void 멤버_생성() {
        Member savedMember = memberRepository.save(new Member("test@example.com", "password", "LOCAL"));

        assertAll(
                () -> assertThat(savedMember.getId()).isNotNull(),
                () -> assertThat(savedMember.getEmail()).isEqualTo("test@example.com")
        );
    }

    @Test
    void 이메일로_멤버_조회() {
        String email = "test@example.com";
        memberRepository.save(new Member(email, "password", "LOCAL"));

        Member foundMember = memberRepository.findByEmail(email).orElseThrow();

        assertThat(foundMember.getEmail()).isEqualTo(email);
    }

    @Test
    void 멤버_삭제() {
        Member savedMember = memberRepository.save(new Member("test@example.com", "password", "LOCAL"));
        Long memberId = savedMember.getId();

        memberRepository.deleteById(memberId);

        assertThat(memberRepository.findById(memberId)).isEmpty();
    }

}
