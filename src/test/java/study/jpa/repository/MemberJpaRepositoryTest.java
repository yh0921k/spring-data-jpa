package study.jpa.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.jpa.entity.Member;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@Rollback(value = false)
class MemberJpaRepositoryTest {

  @Autowired MemberJpaRepository memberJpaRepository;

  @Test
  public void testMember() {
    // given
    Member member = new Member("MemberB");

    // when
    Member savedMember = memberJpaRepository.save(member);
    Member findMember = memberJpaRepository.find(savedMember.getId());

    // then
    assertThat(findMember.getId()).isEqualTo(member.getId());
    assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
    assertThat(findMember).isEqualTo(member);
  }

  @Test
  public void basicCRUD() {
    Member member1 = new Member("Member1");
    Member member2 = new Member("Member2");

    memberJpaRepository.save(member1);
    memberJpaRepository.save(member2);

    // 단건 조회 검증
    Member findMember1 = memberJpaRepository.findById(member1.getId()).get();
    Member findMember2 = memberJpaRepository.findById(member2.getId()).get();
    assertThat(findMember1).isEqualTo(member1);
    assertThat(findMember2).isEqualTo(member2);

    // 리스트 조회 검증
    List<Member> all = memberJpaRepository.findAll();
    assertThat(all.size()).isEqualTo(2);

    // 카운트 검증
    long count = memberJpaRepository.count();
    assertThat(count).isEqualTo(2);

    // 삭제 검증
    memberJpaRepository.delete(member1);
    memberJpaRepository.delete(member2);

    // 카운트 검증
    long deletedCount = memberJpaRepository.count();
    assertThat(deletedCount).isEqualTo(0);
  }

  @Test
  public void findByUsernameAndAgeGreaterThen() {
    Member memberA = new Member("AAA", 10);
    Member memberB = new Member("AAA", 20);

    memberJpaRepository.save(memberA);
    memberJpaRepository.save(memberB);

    List<Member> members = memberJpaRepository.findByUsernameAndAgeGreaterThan("AAA", 15);

    assertThat(members.get(0).getUsername()).isEqualTo("AAA");
    assertThat(members.get(0).getAge()).isEqualTo(20);
    assertThat(members.size()).isEqualTo(1);
  }

  @Test
  public void testNamedQuery() {
    Member memberA = new Member("AAA", 10);

    memberJpaRepository.save(memberA);

    List<Member> members = memberJpaRepository.findByUsername("AAA");

    assertThat(members.get(0).getUsername()).isEqualTo("AAA");
    assertThat(members.get(0).getAge()).isEqualTo(10);
    assertThat(members.size()).isEqualTo(1);
  }
}

