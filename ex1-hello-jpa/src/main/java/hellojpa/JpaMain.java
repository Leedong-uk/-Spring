package hellojpa;

import hellojpa.jpabook.jpashop.domain.*;
import jakarta.persistence.*;

import java.util.List;

public class JpaMain {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            Member member = new Member();
            member.setUserName("member1");
            member.setHomeAddress(new Address("homecity1","street1","qwe123"));

            member.getFavoriteFoods().add("치킨");
            member.getFavoriteFoods().add("족발");
            member.getFavoriteFoods().add("피자");

            member.getAddressHistory().add(new AddressEntity("old1", "street1", "qwe123"));
            member.getAddressHistory().add(new AddressEntity("old2", "street1", "qwe123"));

            em.persist(member);

            em.flush();
            em.clear();

            System.out.println("=============================");
            Member findMember = em.find(Member.class, member.getId());

            //homeCity -> newCity
//            findMember.getHomeAddress().setCity("newCity");
//            Address old = findMember.getHomeAddress();
//            findMember.setHomeAddress(new Address("newCity",old.getStreet(),old.getZipcode()));
//
//
//            //치킨 -> 한식
//            findMember.getFavoriteFoods().remove("치킨");
//            findMember.getFavoriteFoods().add("한식");
//
//
//            //history 주소를 봐꿔보자
//            findMember.getAddressHistory().remove(new Address("old1", "street1", "qwe123"));
//            findMember.getAddressHistory().add(new Address("newCity1", "street1", "qwe123"));

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        }finally {
            em.close();
        }
        emf.close();
    }
}
