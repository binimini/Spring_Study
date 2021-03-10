package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.example.demo.domain.Member;

class MemoryMemberRepositoryTest {
	MemoryMemberRepository repository = new MemoryMemberRepository();
	
	//test ���ַ� test ���� �ۼ��ϰ� ���� �����ϴ� ������ TDD
	@AfterEach
	public void afterEach() {//Test ���� ��� ���� ����� Test ���� ������ ����
		repository.clearstore();
	}
	@Test
	public void save() {
		Member member = new Member();
		repository.save(member);
		Member result = repository.findById(member.getId()).get();//Optional���� �� ������
		org.assertj.core.api.Assertions.assertThat(member).isEqualTo(result);
		
	}
	@Test
	public void findByName() {
		Member member1 = new Member();
		member1.setName("spring1");
		repository.save(member1);

		
		Member member2 = new Member();
		member2.setName("spring2");
		repository.save(member2);
		
		Member result = repository.findByName("spring1").get();
		org.assertj.core.api.Assertions.assertThat(result).isEqualTo(member1);
		
	}
	
	@Test
	public void findAll() {
		Member member1 = new Member();
		member1.setName("spring1");
		repository.save(member1);

		
		Member member2 = new Member();
		member2.setName("spring2");
		repository.save(member2);
		
		List<Member> list=repository.findAll();
		org.assertj.core.api.Assertions.assertThat(list.size()).isEqualTo(2);
	}
}
