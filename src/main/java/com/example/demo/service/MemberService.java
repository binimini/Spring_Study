package com.example.demo.service;

import java.util.List;
import java.util.Optional;

import com.example.demo.domain.Member;
import com.example.demo.repository.MemberRepository;
import com.example.demo.repository.MemoryMemberRepository;

public class MemberService {
	private final MemberRepository memberRepository;
	public MemberService(MemoryMemberRepository memoryMemberRepository) {
		this.memberRepository = memoryMemberRepository;
	}
	//ȸ������
	public Long join(Member member) {
		//���� �̸� �ߺ� X
		validateDuplicateMember(member);
		memberRepository.save(member);
		return member.getId();
		
	}
	private void validateDuplicateMember(Member member) {
		memberRepository.findByName(member.getName())
			.ifPresent(m->{
				throw new IllegalStateException("already exist.");
			});
	}
	
	//��üȸ����ȸ
	public List<Member> findMembers(){
		return memberRepository.findAll();
	}
	
	//ȸ���Ѹ���ȸ
	public Optional<Member> findOne(Long memberId){
		return memberRepository.findById(memberId);
	}
}
