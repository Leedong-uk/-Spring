package study.data_jpa.repository;

import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import study.data_jpa.dto.MemberDto;
import study.data_jpa.entity.Member;
import study.data_jpa.entity.Team;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class MemberRepositoryTest {
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private MemberJpaRepository memberJpaRepository;

    @Autowired
    private EntityManager em;
    @Autowired
    private TeamRepository teamRepository;

    @Test
    void paging() throws Exception{
        //given
        memberRepository.save(new Member("memeber1", 10));
        memberRepository.save(new Member("memeber2", 10));
        memberRepository.save(new Member("memeber3", 10));
        memberRepository.save(new Member("memeber4", 10));
        memberRepository.save(new Member("memeber5", 10));

        int age = 10;
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.Direction.DESC, "username");


        //when
        Page<Member> page = memberRepository.findByAge(age, pageRequest);

        page.map(member -> new MemberDto(member.getId(), member.getUsername(), null));

        //then
        List<Member> content = page.getContent();
        long totalElements = page.getTotalElements();

        assertThat(content.size()).isEqualTo(3);
        assertThat(page.getTotalElements()).isEqualTo(5);
        assertThat(page.getNumber()).isEqualTo(0);
        assertThat(page.getTotalPages()).isEqualTo(2);
        assertThat(page.isFirst()).isTrue();
        assertThat(page.hasNext()).isTrue();

    }

    @Test
    void bulkUpdate() throws Exception{
        //given
        memberRepository.save(new Member("memeber1", 10));
        memberRepository.save(new Member("memeber2", 19));
        memberRepository.save(new Member("memeber3", 20));
        memberRepository.save(new Member("memeber4", 21));
        memberRepository.save(new Member("memeber5", 40));

        //when
        int resultCount = memberJpaRepository.bulkAgePlus(20);

        //then
        assertThat(resultCount).isEqualTo(3);
    }

    @Test
    void bulkupdate() throws Exception{
        //given
        memberRepository.save(new Member("memeber1", 10));
        memberRepository.save(new Member("memeber2", 19));
        memberRepository.save(new Member("memeber3", 20));
        memberRepository.save(new Member("memeber4", 21));
        memberRepository.save(new Member("memeber5", 40));

        //when
        int resultCount = memberRepository.bulkAgePlus(20);
        em.flush();
        em.clear();

        memberRepository.findByUsername("member5");

        //then
        assertThat(resultCount).isEqualTo(3);
    }

    @Test
    void findMemberLazy() throws Exception{
        //given
        // member1 -> teamA
        // member2 -> teamB

        Team teamA = new Team("TeamA");
        Team teamB = new Team("TeamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 10, teamB);
        memberRepository.save(member1);
        memberRepository.save(member2);

        em.flush();
        em.clear();

        //when
        List<Member> members = memberRepository.findAll();
        for (Member member : members) {
            System.out.println("member.getUsername() = " + member.getUsername());
            System.out.println("member.getTeam() = " + member.getTeam());
        }

        //then
     }

     @Test
     void queryHint() throws Exception{
         //given
         Member member1 = memberRepository.save(new Member("member1", 10));
         em.flush();
         em.clear();

         //when
         Member findMember = memberRepository.findReadOnlyByUsername("member1");
         findMember.setUsername("member2");

         em.flush();
         //then
    }


}