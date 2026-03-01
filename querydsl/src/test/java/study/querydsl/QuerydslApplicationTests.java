package study.querydsl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceUnit;
import jakarta.transaction.Transactional;
import jdk.swing.interop.SwingInterOpUtils;
import org.assertj.core.api.Assertions;
import org.hibernate.dialect.TiDBDialect;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import study.querydsl.dto.MemberDto;
import study.querydsl.entity.*;

import java.util.List;

import static com.querydsl.core.types.ExpressionUtils.count;
import static com.querydsl.jpa.JPAExpressions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static study.querydsl.entity.QMember.*;
import static study.querydsl.entity.QTeam.team;

@SpringBootTest
@Transactional
@Commit
class QuerydslApplicationTests {

	@Autowired
	EntityManager em;


	JPAQueryFactory queryFactory;

	@Test
	void contextLoads() {
		Hello hello = new Hello();
		em.persist(hello);

		JPAQueryFactory query = new JPAQueryFactory(em);
		QHello qHello = QHello.hello;
		Hello result = query
				.selectFrom(qHello)
				.fetchOne();

	}

	@BeforeEach
	public void before() {
		queryFactory = new JPAQueryFactory(em);
		Team teamA = new Team("teamA");
		Team teamB = new Team("teamB");
		em.persist(teamA);
		em.persist(teamB);

		Member member1 = new Member("member1", 10, teamA);
		Member member2 = new Member("member2", 10, teamA);

		Member member3 = new Member("member2", 10, teamB);
		Member member4 = new Member("member2", 10, teamB);

		em.persist(member1);
		em.persist(member2);
		em.persist(member3);
		em.persist(member4);
	}
	
	@Test
	@DisplayName("startJPQL")
	void startJpql() throws Exception {
	    //given
		String qlString = "select m from Member m where m.username=:username";
		Member findMember = em.createQuery(qlString, Member.class)
				.setParameter("username", "member1")
				.getSingleResult();

		assertThat(findMember.getUsername()).isEqualTo("member1");
	    
	}


	@Test
	@DisplayName("startQuerydsl")
	void startQuerydsl() throws Exception {
	    //given

		Member findMember = queryFactory.select(member)
				.from(member)
				.where(member.username.eq("member1"))
				.fetchOne();

		assertThat(findMember.getUsername()).isEqualTo("member1");
	}

	@Test
	@DisplayName("TestName")
	void testName() throws Exception {
		QMember member1 =  QMember.member;
		Member findMember = queryFactory.selectFrom(member1)
				.where(member1.username.eq("member1")
						.and(member.age.eq(10)))
				.fetchOne();

	    //then
		assertThat(findMember.getUsername()).isEqualTo("member1");
	}

	@Test
	@DisplayName("SearchAndParam")
	void searchAndParam() throws Exception {

		Member findMember = queryFactory.selectFrom(member)
				.where(
						member.username.eq("member1"),
						(member.age.eq(10))
				)
				.fetchOne();

		//then
		assertThat(findMember.getUsername()).isEqualTo("member1");
	}
	
	@Test
	@DisplayName("resultFetch")
	void resultFetch() throws Exception {
	    //given
		List<Member> fetch = queryFactory.selectFrom(member)
				.fetch();

		Member fetchOne = queryFactory.selectFrom(member).fetchOne();

		Member fetchFirst = queryFactory.selectFrom(member).fetchFirst();
		queryFactory.select(count(member))
				.from(member);

	    
	}

	/**
	 * 1. 나이 내림 차순 (desc)
	 * 2. 회원 이름 올림차순(asc)
	 * 단 2에서 회원 이름이 없으면 마지막에 출력 (null last)
	 */
	@Test
	@DisplayName("sort")
	void sort() throws Exception {
	    //given
		em.persist(new Member(null,100));
		em.persist(new Member("member5",100));
		em.persist(new Member("member6",100));

		//when
		List<Member> result = queryFactory
				.selectFrom(member)
				.where(member.age.eq(100))
				.orderBy(member.age.desc(), member.username.asc().nullsLast())
				.fetch();

		//then
		Member member5 = result.get(0);
		Member member6 = result.get(1);
		Member memberNull = result.get(2);
	}

	@Test
	@DisplayName("paging")
	void paging() throws Exception {
	    //given
		List<Member> result = queryFactory.selectFrom(member)
				.orderBy(member.username.desc())
				.offset(1)
				.limit(2)
				.fetch();

		//then
		assertThat(result.size()).isEqualTo(2);
	}

	@Test
	@DisplayName("aggregation")
	void aggregation() throws Exception {
	    //given
		List<Tuple> result = queryFactory.select(
						member.count(),
						member.age.sum(),
						member.age.avg(),
						member.age.max(),
						member.age.min()
				)
				.from(member)
				.fetch();

		//when
		Tuple tuple = result.get(0);
		System.out.println(tuple); // -> [4,40,10.0,10,10]

		//tuple 이라서 KEY 로 꺼내면 됨
		tuple.get(member.count());
		tuple.get(member.age.sum());
		tuple.get(member.age.avg());
		tuple.get(member.age.max());
		tuple.get(member.age.min());

	}


	/**
	 * 팀의 이름과 각 팀의 평균 연령을 구해라
	 */
	@Test
	@DisplayName("group")
	void group() throws Exception {
	    //given
		List<Tuple> result = queryFactory.select(team.name, member.age.avg())
				.from(member)
				.join(member.team, team)
				.groupBy(team.name)
				.fetch();

		//when
		Tuple teamA = result.get(0);
		Tuple teamB = result.get(1);

		//then
		assertThat(teamA.get(team.name)).isEqualTo("teamA");
	}

	@Test
	@DisplayName("join")
	void join() throws Exception {
	    //given
		List<Member> result = queryFactory.selectFrom(member)
				.join(member.team, team)
				.where(team.name.eq("teamA"))
				.fetch();

		//when


	    //then

	}


	/**
	 * 세타 조인
	 * 회원의 이름이 팀 이름과 같은 회원 조회
	 */
	@Test
	@DisplayName("theta_join")
	void thetaJoin() throws Exception {
	    //given
		em.persist(new Member("teamA"));
		em.persist(new Member("teamB"));
	            
	    //when
		List<Member> result = queryFactory
				.select(member)
				.from(member, team)
				.where(member.username.eq(team.name))
				.fetch();

		//then
	    
	}

	/**
	 * 예) 회원과 팀을 조인하면서 , 팀 이름이 teamA 인 팀만 조인 , 회원은 모두 조회
	 */
	@Test
	@DisplayName("join_on_filtering")
	void joinOnFiltering() throws Exception {
	    //given
		List<Tuple> result = queryFactory.select(member,team)
				.from(member)
				.join(member.team, team)
				.on(team.name.eq("teamA"))
				.fetch();

		for (Tuple tuple : result) {
			System.out.println("tuple = " + tuple);
		}
	    
	}

	/**
	 * 연관 관게가 없는 엔티티 외부 조인
	 * 회원의 이름이 팀 이름과 같은 회원 조회
	 */
	@Test
	void join_on_no_realtaion() throws Exception {
		//given
		em.persist(new Member("teamA"));
		em.persist(new Member("teamB"));

		//when
		List<Member> result = queryFactory
				.select(member)
				.from(member)
				.leftJoin(team).on(member.username.eq(team.name))
				.fetch();

		//then

	}

	@PersistenceUnit
	EntityManagerFactory emf;

	@Test
	@DisplayName("fetchJoinNo")
	void fetchJoinNo() throws Exception {
	    //given
	    em.flush();
		em.clear();

		Member findMember = queryFactory.selectFrom(member)
				.where(member.username.eq("member1"))
				.fetchOne();


		boolean loaded = emf.getPersistenceUnitUtil().isLoaded(findMember.getTeam());
		assertThat(loaded).as("패치 조인 미적용").isFalse();

	}

	@Test
	@DisplayName("fetchJoinUse")
	void fetchJoinUse() throws Exception {
	    //given
		Member findMember = queryFactory.selectFrom(member)
				.join(member.team, team).fetchJoin()
				.where(member.username.eq("member1"))
				.fetchOne();
		boolean loaded = emf.getPersistenceUnitUtil().isLoaded(findMember.getTeam());

		assertThat(loaded).as("패치 조인 적용").isTrue();

	}

	/**
	 * 나이가 가장 많은 회원 조회
	 */
	@Test
	@DisplayName("subQuery")
	void subQuery() throws Exception {
	    //given

		QMember memberSub = new QMember("memberSub");

		List<Member> result = queryFactory
				.selectFrom(member)
				.where(member.age.eq(
						select(memberSub.age.max())
								.from(memberSub)
				))
				.fetch();

	    //then
		assertThat(result).extracting("age")
				.containsExactly(40);

	}


	/**
	 * 나이가 평균 이상인  회원 조회
	 */
	@Test
	@DisplayName("subQueryGoe")
	void subQueryGoe() throws Exception {
		//given

		QMember memberSub = new QMember("memberSub");

		List<Member> result = queryFactory
				.selectFrom(member)
				.where(member.age.goe(
						select(memberSub.age.avg())
								.from(memberSub)
				))
				.fetch();

		//then
		assertThat(result).extracting("age")
				.containsExactly(40);

	}

	@Test
	@DisplayName("selectSubQuery")
	void selectSubQuery() throws Exception {
	    //given
		QMember memberSub = new QMember("memberSub");
		queryFactory.select(member.username,
						select(memberSub.age.avg())
								.from(memberSub)).
					from(member)
					.fetch();

	    //when


	    //then

	}

	@Test
	@DisplayName("baisCase")
	void baisCase() throws Exception {
	    //given
		List<String> result = queryFactory
				.select(member.age
						.when(10).then("열살")
						.when(20).then("스무살")
						.otherwise("기타"))
				.fetch();

		//when


	    //then

	}

	@Test
	@DisplayName("constant")
	void constant() throws Exception {
	    //given
		List<Tuple> result = queryFactory
				.select(member.username, Expressions.constant("A"))
				.from(member)
				.fetch();

		for (Tuple tuple : result) {
			System.out.println("tuple = " + tuple);
		}
	}
	
	@Test
	@DisplayName("concat")
	void concat() throws Exception {
	    //given
		List<String> result = queryFactory.select(member.username.concat("_").concat(member.age.stringValue()))
				.from(member)
				.where(member.username.eq("member1"))
				.fetch();

		for (String s : result) {
			System.out.println("s = " + s);
		}
	}

	@Test
	@DisplayName("simpleProjection")
	void simpleProjection() throws Exception {
	    //given
		List<String> result = queryFactory.select(member.username)
				.from(member)
				.fetch();
	}
	
	@Test
	@DisplayName("tuplePorjection")
	void tuplePorjection() throws Exception {
	    //given
		List<Tuple> result = queryFactory.select(member.username, member.age)
				.from(member)
				.fetch();

		for (Tuple tuple : result) {
			String username = tuple.get(member.username);
			Integer age = tuple.get(member.age);
		}
	}

	@Test
	@DisplayName("findDtoBySetter")
	void findDtoBySetter() throws Exception {
	    //given
		List<MemberDto> result = queryFactory.select(Projections.bean(MemberDto.class,
						member.username,
						member.age))
				.from(member)
				.fetch();

	}

	@Test
	@DisplayName("findDtoByField")
	void findDtoByField() throws Exception {
	    //given
		queryFactory.select(Projections.fields(MemberDto.class,
						member.username,
						member.age))
				.from(member);


	}

	@Test
	@DisplayName("findDtoByConstructor")
	void findDtoByConstructor() throws Exception {
		//given
		List<MemberDto> result = queryFactory.select(Projections.constructor(MemberDto.class,
						member.username,
						member.age))
				.from(member)
				.fetch();

	}


	@Test
	@DisplayName("findDto")
	void findDto() throws Exception {
		//given
		List<MemberDto> result = queryFactory.select(Projections.fields(MemberDto.class,
						member.username.as("name"),
						member.age))
				.from(member)
				.fetch();

	}

	@Test
	@DisplayName("dynamicQuery_BooleanBuilder")
	void dynamicQueryBooleanBuilder() throws Exception {
	    //given
		String usernameParam = "member1";
		Integer ageParam = 10;

		List<Member> result = searchMember1(usernameParam,ageParam);


	    //when


	    //then

	}

    private List<Member> searchMember1(String usernameCond, Integer ageCond) {
		BooleanBuilder builder = new BooleanBuilder();
		if (usernameCond != null) {
			builder.and(member.username.eq(usernameCond));
		}

		if (ageCond != null) {
			builder.and(member.age.eq(ageCond));
		}



		return queryFactory
				.selectFrom(member)
				.where(builder)
				.fetch();
    }

	@Test
	@DisplayName("dynamicQuery_WhereParam")
	void dynamicQueryWhereParam() throws Exception {
	    //given
		String usernameParam = "member1";
		Integer ageParam = 10;

		List<Member> result = searchMember2(usernameParam,ageParam);


		//when


	    //then

	}

    private List<Member> searchMember2(String usernameCond, Integer ageCond) {
		return queryFactory
				.selectFrom(member)
				.where(allEq(usernameCond,ageCond))
				.fetch();
    }

	private BooleanExpression usernameEq(String usernameCond) {
		if (usernameCond == null) {
			return null;
		}
		return member.username.eq(usernameCond);
    }

	private BooleanExpression ageEq(Integer ageCond) {
		if(ageCond == null)
			return null;
		return member.age.eq(ageCond);
	}

	private BooleanExpression allEq(String usernameCond, Integer ageCond) {
		return usernameEq(usernameCond).and(ageEq(ageCond));
	}
	
	@Test
	@DisplayName("bulkUpdate")
	void bulkUpdate() throws Exception {
	    //given
		long count = queryFactory.update(member)
				.set(member.username, "비회원")
				.where(member.age.lt(28))
				.execute();

	    
	}

	@Test
	@DisplayName("bulkAdd")
	void bulkAdd() throws Exception {
	    //given
		queryFactory.update(member)
				.set(member.age, member.age.add(1))
				.execute();
	}
	
	@Test
	@DisplayName("bulkMultiply")
	void bulkMultiply() throws Exception {
	    //given
		queryFactory.update(member)
				.set(member.age, member.age.multiply(2))
				.execute();
		
	}
	
	@Test
	@DisplayName("bulkDelete")
	void bulkDelete() throws Exception {
	    //given
		queryFactory.delete(member)
				.where(member.age.gt(18))
				.execute();
	            
	    //when
	
	    
	    //then
	    
	}

	@Test
	@DisplayName("sqlFunction")
	void sqlFunction() throws Exception {
	    //given
		String result = queryFactory
				.select(Expressions.stringTemplate("function('replace', {0}, {1}, {2})",
						member.username, "member", "M"))
				.from(member)
				.fetchFirst();

	    //when


	    //then

	}

	

	

}
