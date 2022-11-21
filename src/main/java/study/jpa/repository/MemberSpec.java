package study.jpa.repository;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;
import study.jpa.entity.Member;
import study.jpa.entity.Team;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;

public class MemberSpec {

  public static Specification<Member> teamName(final String teamName) {
    return (root, query, criteriaBuilder) -> {
      if (StringUtils.isEmpty(teamName)) {
        return null;
      }

      Join<Member, Team> t = root.join("team", JoinType.INNER);
      return criteriaBuilder.equal(t.get("name"), teamName);
    };
  }

  public static Specification<Member> username(final String username) {
    return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("username"), username);
  }
}
