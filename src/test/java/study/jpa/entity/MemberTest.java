package study.jpa.entity;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.jpa.repository.MemberRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@SpringBootTest
@Transactional
@Rollback(value = false)
class MemberTest {

  @PersistenceContext EntityManager em;
  @Autowired
  MemberRepository memberRepository;

  @Test
  public void testEntity() {
    Team teamA = new Team("teamA");
    Team teamB = new Team("teamB");
    em.persist(teamA);
    em.persist(teamB);

    Member member1 = new Member("Member1", 10, teamA);
    Member member2 = new Member("Member2", 10, teamA);
    Member member3 = new Member("Member3", 10, teamB);
    Member member4 = new Member("Member4", 10, teamB);
    em.persist(member1);
    em.persist(member2);
    em.persist(member3);
    em.persist(member4);

    em.flush();
    em.clear();

    List<Member> members = em.createQuery("select m from Member m", Member.class).getResultList();
    for (Member member : members) {
      System.out.println("member = " + member);
      System.out.println("member.getTeam() = " + member.getTeam());
    }
  }

  @Test
  public void JpaEventBaseEntity() throws Exception {
    // given
    Member member = new Member("MemberA");
    memberRepository.save(member);

    Thread.sleep(100);
    member.setUsername("MemberB");

    em.flush();
    em.clear();

    // when
    Member findMember = memberRepository.findById(member.getId()).get();

    // then
    System.out.println("findMember.getCreatedDate() = " + findMember.getCreatedDate());
    System.out.println("findMember.getUpdatedDate() = " + findMember.getLastModifiedDate());
    System.out.println("findMember.getCreateBy() = " + findMember.getCreateBy());
    System.out.println("findMember.getLastModifiedBy() = " + findMember.getLastModifiedBy());
  }
}
