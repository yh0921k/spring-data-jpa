package study.jpa.entity;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@SpringBootTest
@Transactional
@Rollback(value = false)
class MemberTest {

  @PersistenceContext EntityManager em;

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
}
