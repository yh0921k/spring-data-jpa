package study.jpa.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.jpa.dto.MemberDto;
import study.jpa.entity.Member;
import study.jpa.entity.Team;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@Rollback(false)
class MemberRepositoryTest {

  @Autowired MemberRepository memberRepository;
  @Autowired TeamRepository teamRepository;
  @PersistenceContext EntityManager em;

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

  @Test
  public void returnType() {
    Member memberA = new Member("AAA", 10);
    Member memberB = new Member("BBB", 20);

    memberRepository.save(memberA);
    memberRepository.save(memberB);

    List<Member> listByUsername = memberRepository.findListByUsername("AAA");
    assertThat(listByUsername.get(0).getUsername()).isEqualTo("AAA");

    Member memberByUsername = memberRepository.findMemberByUsername("AAA");
    assertThat(memberByUsername.getUsername()).isEqualTo("AAA");

    Optional<Member> optionalByUsername = memberRepository.findOptionalByUsername("BBB");
    assertThat(optionalByUsername.get().getUsername()).isEqualTo("BBB");
  }

  @Test
  public void paging() {
    // given
    memberRepository.save(new Member("member1", 10));
    memberRepository.save(new Member("member2", 10));
    memberRepository.save(new Member("member3", 10));
    memberRepository.save(new Member("member4", 10));
    memberRepository.save(new Member("member5", 10));
    memberRepository.save(new Member("member6", 10));
    memberRepository.save(new Member("member7", 10));
    memberRepository.save(new Member("member8", 10));

    int age = 10;
    PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));

    // when
    Page<Member> page = memberRepository.findByAge(age, pageRequest);

    Page<MemberDto> toMap = page.map(m -> new MemberDto(m.getId(), m.getUsername(), ""));

    // then
    List<Member> content = page.getContent();
    assertThat(content.get(0).getUsername()).isEqualTo("member8");
    assertThat(content.get(1).getUsername()).isEqualTo("member7");

    long totalElements = page.getTotalElements();
    assertThat(totalElements).isEqualTo(8);

    assertThat(page.getNumber()).isEqualTo(0);
    assertThat(page.getTotalPages()).isEqualTo(3);
    assertThat(page.isFirst()).isTrue();
    assertThat(page.hasNext()).isTrue();
  }

  @Test
  public void slice() {
    // given
    memberRepository.save(new Member("member1", 10));
    memberRepository.save(new Member("member2", 10));
    memberRepository.save(new Member("member3", 10));
    memberRepository.save(new Member("member4", 10));
    memberRepository.save(new Member("member5", 10));
    memberRepository.save(new Member("member6", 10));
    memberRepository.save(new Member("member7", 10));
    memberRepository.save(new Member("member8", 10));

    int age = 10;
    PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));

    // when
    Slice<Member> page = memberRepository.findSliceByAge(age, pageRequest);

    // then
    List<Member> content = page.getContent();
    assertThat(content.get(0).getUsername()).isEqualTo("member8");
    assertThat(content.get(1).getUsername()).isEqualTo("member7");

    assertThat(page.getNumber()).isEqualTo(0);
    assertThat(page.isFirst()).isTrue();
    assertThat(page.hasNext()).isTrue();
  }

  @Test
  public void separateCountQuery() {
    // given
    memberRepository.save(new Member("member1", 10));
    memberRepository.save(new Member("member2", 10));
    memberRepository.save(new Member("member3", 10));
    memberRepository.save(new Member("member4", 10));

    PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));

    // when
    Page<Member> page = memberRepository.findMemberAllCountBy(pageRequest);
    assertThat(page.getTotalElements()).isEqualTo(4);
  }

  @Test
  public void bulkUpdate() {
    // given
    memberRepository.save(new Member("member1", 10));
    memberRepository.save(new Member("member2", 19));
    memberRepository.save(new Member("member3", 20));
    memberRepository.save(new Member("member4", 21));
    memberRepository.save(new Member("member5", 40));

    // when
    int resultCount = memberRepository.bulkAgePlus(20);

    List<Member> findMembers = memberRepository.findByUsername("member5");

    // then
    assertThat(resultCount).isEqualTo(3);
    assertThat(findMembers.get(0).getAge()).isEqualTo(41);
  }

  @Test
  public void findMemberLazy() {
    
    // given
    Team teamA = new Team("TeamA");
    Team teamB = new Team("TeamB");
    teamRepository.save(teamA);
    teamRepository.save(teamB);

    Member member1 = new Member("Member1", 10, teamA);
    Member member2 = new Member("Member2", 10, teamB);
    memberRepository.save(member1);
    memberRepository.save(member2);

    em.flush();
    em.clear();
    
    // when
    List<Member> members = memberRepository.findAll();
    for (Member member : members) {
      System.out.println("member.getUsername() = " + member.getUsername());

      // Team 조회를 위한 추가 쿼리 발생(N + 1)
      System.out.println("member.getTeam().getClass() = " + member.getTeam().getClass());
      System.out.println("member.getTeam().getName() = " + member.getTeam().getName());
    }
  }

  @Test
  public void findMemberFetchJoin() {

    // given
    Team teamA = new Team("TeamA");
    Team teamB = new Team("TeamB");
    teamRepository.save(teamA);
    teamRepository.save(teamB);

    Member member1 = new Member("Member1", 10, teamA);
    Member member2 = new Member("Member2", 10, teamB);
    memberRepository.save(member1);
    memberRepository.save(member2);

    em.flush();
    em.clear();

    // when
    List<Member> members = memberRepository.findMemberFetchJoin();
    for (Member member : members) {
      System.out.println("member.getUsername() = " + member.getUsername());

      // Team 조회를 위한 추가 쿼리 발생(N + 1)
      System.out.println("member.getTeam().getClass() = " + member.getTeam().getClass());
      System.out.println("member.getTeam().getName() = " + member.getTeam().getName());
    }
  }

  @Test
  public void queryHint() {

    // given
    Member member1 = new Member("Member1", 10);
    memberRepository.save(member1);
    em.flush(); // 영속성 컨텍스트에는 데이터가 남아있음
    em.clear(); // 영속성 컨텍스트 초기화(1차 캐시 없음)

    // when
    Member findMember = memberRepository.findReadOnlyByUsername("Member1");
    findMember.setUsername("Member2");
    em.flush(); // 변경 감지가 동작하지 않으며, 내부적으로 스냅샷을 만들지 않음
  }

  @Test
  public void lock() {

    // given
    Member member1 = new Member("Member1", 10);
    memberRepository.save(member1);
    em.flush();
    em.clear();

    // when
    List<Member> members = memberRepository.findLockByUsername("Member1");
  }

  @Test
  public void callCustom() {
    Member member1 = new Member("Member1", 10);
    memberRepository.save(member1);

    em.flush();
    em.clear();

    List<Member> memberCustom = memberRepository.findMemberCustom();
    for (Member member : memberCustom) {
      System.out.println("member = " + member);
    }
  }

  @Test
  public void specBasic() {
    // given
    Team team = new Team("TeamA");
    em.persist(team);

    Member m1 = new Member("m1", 0, team);
    Member m2 = new Member("m2", 0, team);
    em.persist(m1);
    em.persist(m2);

    em.flush();
    em.clear();

    // when
    Specification<Member> spec = MemberSpec.username("m1").and(MemberSpec.teamName("TeamA"));
    List<Member> result = memberRepository.findAll(spec);

    // then
    assertThat(result.size()).isEqualTo(1);
  }

  @Test
  public void queryByExample() {
    // given
    Team team = new Team("TeamA");
    em.persist(team);

    Member m1 = new Member("m1", 0, team);
    Member m2 = new Member("m2", 0, team);
    em.persist(m1);
    em.persist(m2);

    em.flush();
    em.clear();

    // when

    // Probe
    Member condition = new Member("m1");
    Team conditionTeam = new Team("TeamA");
    condition.setTeam(conditionTeam);

    ExampleMatcher matcher = ExampleMatcher.matching().withIgnorePaths("age");
    Example<Member> example = Example.of(condition, matcher);

    List<Member> result = memberRepository.findAll(example);

    // then
    assertThat(result.size()).isEqualTo(1);
  }

  @Test
  public void projections() {
    // given
    Team team = new Team("TeamA");
    em.persist(team);

    Member m1 = new Member("m1", 0, team);
    Member m2 = new Member("m2", 0, team);
    em.persist(m1);
    em.persist(m2);

    em.flush();
    em.clear();

    List<NestedClosedProjections> result = memberRepository.findProjectionsByUsername("m1", NestedClosedProjections.class);
    for (NestedClosedProjections nestedClosedProjections : result) {
      System.out.println("nestedClosedProjections.getUsername() = " + nestedClosedProjections.getUsername());
      System.out.println("nestedClosedProjections.getTeam().getName() = " + nestedClosedProjections.getTeam().getName());
    }
  }

  @Test
  public void nativeQuery() {
    // given
    Team team = new Team("TeamA");
    em.persist(team);

    Member m1 = new Member("m1", 0, team);
    Member m2 = new Member("m2", 0, team);
    em.persist(m1);
    em.persist(m2);

    em.flush();
    em.clear();

    Member member = memberRepository.findByNativeQuery("m1");
    System.out.println("member = " + member);
  }

  @Test
  public void nativeProjection() {
    // given
    Team team = new Team("TeamA");
    em.persist(team);

    Member m1 = new Member("m1", 0, team);
    Member m2 = new Member("m2", 0, team);
    em.persist(m1);
    em.persist(m2);

    em.flush();
    em.clear();

    Page<MemberProjection> byNativeProjection = memberRepository.findByNativeProjection(PageRequest.of(0, 10));
    for (MemberProjection memberProjection : byNativeProjection.getContent()) {
      System.out.println("memberProjection.getId() = " + memberProjection.getId());
      System.out.println("memberProjection.getUsername() = " + memberProjection.getUsername());
      System.out.println("memberProjection.getTeamName() = " + memberProjection.getTeamName());
    }
  }
}
