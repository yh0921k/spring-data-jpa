package study.jpa.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.jpa.dto.MemberDto;
import study.jpa.entity.Member;
import study.jpa.entity.Team;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@Rollback(false)
class MemberRepositoryTest {

  @Autowired MemberRepository memberRepository;
  @Autowired TeamRepository teamRepository;

  @Test
  public void testMember() {
    // given
    Member member = new Member("MemberA");

    // when
    Member savedMember = memberRepository.save(member);
    Member findMember = memberRepository.findById(savedMember.getId()).get();

    // then
    assertThat(findMember.getId()).isEqualTo(member.getId());
    assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
    assertThat(findMember).isEqualTo(member);
  }

  @Test
  public void basicCRUD() {
    Member member1 = new Member("Member1");
    Member member2 = new Member("Member2");

    memberRepository.save(member1);
    memberRepository.save(member2);

    // 단건 조회 검증
    Member findMember1 = memberRepository.findById(member1.getId()).get();
    Member findMember2 = memberRepository.findById(member2.getId()).get();
    assertThat(findMember1).isEqualTo(member1);
    assertThat(findMember2).isEqualTo(member2);

    // 리스트 조회 검증
    List<Member> all = memberRepository.findAll();
    assertThat(all.size()).isEqualTo(2);

    // 카운트 검증
    long count = memberRepository.count();
    assertThat(count).isEqualTo(2);

    // 삭제 검증
    memberRepository.delete(member1);
    memberRepository.delete(member2);

    // 카운트 검증
    long deletedCount = memberRepository.count();
    assertThat(deletedCount).isEqualTo(0);

  }

  @Test
  public void findByUsernameAndGreaterThan() {
    Member memberA = new Member("AAA", 10);
    Member memberB = new Member("AAA", 20);

    memberRepository.save(memberA);
    memberRepository.save(memberB);

    List<Member> members = memberRepository.findByUsernameAndAgeGreaterThan("AAA", 15);

    assertThat(members.get(0).getUsername()).isEqualTo("AAA");
    assertThat(members.get(0).getAge()).isEqualTo(20);
    assertThat(members.size()).isEqualTo(1);
  }

  @Test
  public void testNamedQuery() {
    Member memberA = new Member("AAA", 10);

    memberRepository.save(memberA);

    List<Member> members = memberRepository.findByUsername("AAA");

    assertThat(members.get(0).getUsername()).isEqualTo("AAA");
    assertThat(members.get(0).getAge()).isEqualTo(10);
    assertThat(members.size()).isEqualTo(1);
  }

  @Test
  public void testQuery() {
    Member memberA = new Member("AAA", 10);
    Member memberB = new Member("BBB", 20);

    memberRepository.save(memberA);
    memberRepository.save(memberB);

    List<Member> members = memberRepository.findUser("AAA", 10);

    assertThat(members.get(0).getUsername()).isEqualTo("AAA");
    assertThat(members.get(0).getAge()).isEqualTo(10);
    assertThat(members.size()).isEqualTo(1);
  }

  @Test
  public void findUsernameList() {
    Member memberA = new Member("AAA", 10);
    Member memberB = new Member("BBB", 20);

    memberRepository.save(memberA);
    memberRepository.save(memberB);

    List<String> members = memberRepository.findUsernameList();

    assertThat(members.contains("AAA")).isTrue();
    assertThat(members.contains("BBB")).isTrue();
    assertThat(members.contains("CCC")).isFalse();
  }

  @Test
  public void findMemberDto() {
    Team teamA = new Team("TeamA");
    teamRepository.save(teamA);

    Member memberA = new Member("AAA", 10);
    memberA.setTeam(teamA);
    memberRepository.save(memberA);

    List<MemberDto> memberDtos = memberRepository.findMemberDto();

    assertThat(memberDtos.get(0).getUsername()).isEqualTo("AAA");
    assertThat(memberDtos.get(0).getTeamName()).isEqualTo("TeamA");
  }

  @Test
  public void findByNames() {
    Member memberA = new Member("AAA", 10);
    Member memberB = new Member("BBB", 20);

    memberRepository.save(memberA);
    memberRepository.save(memberB);

    List<Member> members = memberRepository.findByNames(Arrays.asList("AAA", "BBB"));

    assertThat(members.get(0).getUsername()).isEqualTo("AAA");
    assertThat(members.get(1).getUsername()).isEqualTo("BBB");
  }
}