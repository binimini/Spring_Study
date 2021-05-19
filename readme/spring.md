Spring
==

[1.Structure](#구조)

[2.DB](#db)

[3.JPA](#jpa)

[3-1.외래키 매핑](#연관관계-매핑-외래키-매핑)

[3-2.Controller](#컨트롤러-controller)

[3-2-1.ControllerAdvice](#controller-advice)

[3-3.Service](#)

[3-4.Repository](#저장소-repository)

[3-5.Entity](#객체-entity))

[3-5-1.ResponseEntity](#responseentity)

[3-6.Paging](#paging)

[3-7](#query)

[4.Transaction](#transaction)

## 구조

* 프레젠테이션 계층 @Controller
* 서비스 계층 @Service
* 데이터 엑세스 계층 @Repository
* 도메인 모델 계층 @Entity

Controller-Service-Domain-Repository 구성

DAO : 실제로 DB에 접근하는 객체, Service와 DB 사이 고리, Repository

DTO : 데이터 전달을 위한 객체, 로직 없이 getter setter 등만 가지는 순수 데이터 객체, entity 

## DB
* JDBC
* JDBC Template
* [JPA](#jpa)

DB 연동 시 gradle의 dependency, application.properties(or application.yml)에 설정 필요

## JPA
JPA : ORM 사용 명세 API : Interface : 구현체 Hibernate 등

ORM : 객체 - table mapping, 객체와 쿼리문 분리

Spring Data JPA : EntityMapper 대신 Repository Inteface 사용



### 연관관계 매핑 외래키 매핑

jpa : 연관관계 매핑시 entity를 참조
(mybatis : 관계 table PK 참조)

복합키 사용하는 entity : @Id는 단일키만 가능, 복합키 정의를 위한 class(식별자) 필요(@Id 여전히 표시)
* @EmbeddedId : 변수 작성 부분 많음
* @Idclass : 식별자 작성 부분 많음

@Idclass 구현 시 Serializable 상속해야함, 식별자와 엔티티 변수명 같을 것, public이여야 하고, @Data(equals/hashCode), @NoArgsConstructor(default consturtor)

Object 자료형으로 쓸 수 없으므로 Integer로 해놓고 entity에서 중복 매핑한다. (insertable=false, updatable=false)

연관관계를 매핑 시 다중성을 나타내는 어노테이션필수 사용, 엔티티 자신 기준으로 다중성 표시 (1:N, N:1, N:N, 1:1)
    

    @ManyToOne 
    @JoinColumn(name="<매핑할 컬럼 변수명") 

    @OneToMany(mappedBy="<매핑된 객체 변수명>")
    

    @ManyToMany
    @JoinTable(name = "<매개테이블 이름>", joinColumns = @JoinColumn(name = "<현재 객체 테이블에서 매핑되는 컬럼>"),
                        inverseJoinColumns = @JoinColumn(name = "<반대 객체 테이블에서 매핑되는 컬럼>"))
    @ManyToMany(mappedBy = "members")

@ManyToOne @JoinColumn으로 어떤 외래키와 매핑하는 지 표시, 생략 시 기본으로 "필드명_참조테이블의id컬럼명"

@OneToMany @mappedBy 통해서 연관관계의 주인(외래키 가진 테이블 객체) 설정

1 : MappedBy, parent

|

N : 연관관계 주인, child, 외래키 보유

JPA @ManyToMany 관계에서 두 테이블의 키 복합키로 갖는 테이블 자동으로 생성

mappedBy 옵션 통해서 주인 설정


Fetch Type
* Eager : 연관관계 엔티티 모두 가져온다
* Lazy : 연관 관계에 있는 Entity 가져오지 않고, getter 로 접근할 때 가져온다

기본적으로 fetchType.Lazy 사용하자.

Eager 사용시 child 이용시 관련된 parent까지 persistance context에 올리기 때문에 

만약 delete하거나 update한다해도 context에 남아있어서 문제될 수 있다.

Cascade @ManyToMany, @OneToMany(One(Parent)-Many(Child)) 등에서 객체가 변화함에 따라 연관된 객체를 어떻게 할지 정한다

parent -> child
* ALL : 밑에 서술 모두 포함
* PERSIST : 부모 객체가 영속성을 지닐 때 자식 객체도 같이 영속성 지니게 함, 설정 시 자식 객체만 직접적으로 삭제 안 됨 
* REMOVE : 부모 객체가 삭제될 시 자식 객체 같이 삭제

PERSIST 자식 객체 관리 부모가 하는 느낌?

### 컨트롤러 Controller
    @Controller

가장 처음 사용자의 경로 제어하는 부분
    @RequestMapping("/경로")

를 사용해서 Controller 자체의 경로를 설정하거나 메소드의 경로를 설정할 수 있다

    @RestController

API 형식으로 컨트롤러를 사용하고자 할 경우 @Controller 대신 @RestController 사용으로 Circular view error 방지할 수 있다

URI 중 쿼리스트링이 /경로?name=<> 식으로 들어오면 아래와 같이 받는다

    public void function(@RequstParam name)

요청

@RequestBody : Http 요청의 Body내용을 Java Object로 변환시켜주는 역할, POST와 함께 사용

@RequestParam : url에서 요청 파라미터를 1:1로 받을 수 있음 (Defaultvalue, Required 설정 가능)

    (@RequestParam("<url상 변수명>") type name)
    (@RequestBody(""))


응답

@ResponseBody로 객체 넘길 경우 MessageConverter(Jackson)에 의해 JSON으로 변환

    @ResponseBody

#### Controller Advice
전체 Controller에 공통적으로 적용할 수 있다

Exception handler 구현에 사용된다 : 공통적인 예외사항에 대해서 별도로 분리하여 처리하는 방식

### 저장소 Repository

    @Repository
    public interface repository extends JpaRepository <Entity, Primary Key Type> {}
    
### 객체 Entity
JPA 변경 감지 : 영속성 있는 entity 변경 시 변경을 감지하고 트랙잭션 하거나 enitity manager의 flush() 호출하는 경우 자동으로 update 됨

영속 : 엔티티 매니저가 관리하는 persistence context에 엔티티가 저장된 상태

persist() 호출, find(), JPQ, QueryDSL로 entity 조회 시 영속 상태가 됨

update의 경우 트랜잭션 커밋까지 반영 안되고 영속성 context에 기존 값 남아있을 경우 우선되므로 원하는 쿼리 결과 나오지 않을 수 있다

save() 기존에 없는 객체면 persist하고 있으면 merge하므로 set() 후 save() 하면 해결?

primary key AUTO_INCREMENT 설정시 save 후에 primary key 생성되므로 주의


    @Getter
    @RequiredArgsConstructor //NonNull 설정 파라미터 포함한 생성자
    
    @Entity
    @Table(name="") //생략시 객체 클래스 이름 == 테이블 이름
    public class Object{
         @Id                                                 //PRIMARY KEY
         @GeneratedValue(strategy = GenerationType.IDENTITY) //AUTO_INCREMENT
         private Long id;
         @NonNull                                            //lombok
         @Column(nullable = false)
         private String name;
    }

객체 JSON으로 반환 시 양방향 관계 객체의 경우 순환 참조 문제가 발생된다
아래 어노테이션 이용해서 순환 참조 방지

또는 @JsonIgnore를 통해 객체를 반환해도 json에 해당 필드가 포함되지 않게 할 수 있다
    
    @JsonBackReference 
    @JsonManagedReference 
    @JsonIgnore

#### ResponseEntity
ResponseEntity : server에서 status code, body(Object), headers를 반환할 수 있다 (httpStatus는 필수)

error에 대한 responseEntity는 error response 객체를 따로 선언해 통일해 exception handler를 통해 responseEntity<errorresponse>를 반환하는 것으로 처리

responseEntity<Object>를 통해 error message(String), resource(Entity) 전달할 수도 있지만 이 경우 Casting 해줘야할 수 있다

### Paging
Paging : entity를 page로 나눠서 반환하게 하는 것

PageRequest : Serializable, Pageable 상속, Repository에 paging 요청하는 데에 사용

    PageRequest(int page, int size[,Sort sort])

    repository.findAll(Pageable pageable).getContent          //PageRequst 파라미터로, getContent Page->List

Controller에서 파라미터 Pageable 설정 시 size, page, (Sort) 받아서 Pageable 객체 자동으로 생성해 함수의 파라미터로 들어오게 됨

Pageable/PageRequest immutable 객체이므로 변경 사항 적용시 새로 만들어서 사용

### Query
파라미터 바인딩 : 위치 기반/이름 기반

이름 기반 : :name으로 변수 표시, @Param("name") 통해 파라미터 바인딩

    @Query(value = "select u from User u where u.name = :name")
    List<User> findByuserNamedQuery(@Param("name") String name);

## Transaction
Transaction : db에 결과 반영되는 흐름

Session : 하나의 영속성 context 유지, transaction 시작 - 끝

@Transactional 어노테이션 트랙젝션 조작할 수 있다

lazy fetch시 객체의 proxy 반환 만약 다른 session에서 해당 proxy를 통해 객체를 사용하려 하면 

해당 proxy를 intialize 하기 위한 session이 없어 intialize 할 수 없다 => error 

findOne(Entity) vs getOne(Proxy)

ex) lazy fetch 사용시 One - Many 양방향 관계에서 One을 request하고 필요한 처리를 하면 해당 트랙색션을 커밋하고 session을 end

그 뒤에 One을 통해 Many를 호출하게 되면 이미 One session closed => error 

=> 두 처리를 @Transactional 함수 내 처리 시 같은 session 사용할 수 있음?