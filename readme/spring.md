Spring
==

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

* [외래키 매핑](#연관관계-매핑-외래키-매핑)
* [Controller](#컨트롤러-controller)
* [Service](#)
* [Repository](#저장소-repository)
* [Entity](#객체-entity))

#### 연관관계 매핑 외래키 매핑

jpa : 연관관계 매핑시 entity를 참조
(mybatis : 관계 table PK 참조)

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

JPA @ManyToMany 관계에서 두 테이블의 키 복합키로 갖는 테이블 자동으로 생성

mappedBy 옵션 통해서 주인 설정


Fetch Type
* Eager : 연관관계 엔티티 모두 가져온다
* Lazy : 연관 관계에 있는 Entity 가져오지 않고, getter 로 접근할 때 가져온다
-> N+1 Problem?

Cascade @ManyToMany, @OneToMany(One(Parent)-Many(Child)) 등에서 객체가 변화함에 따라 연관된 객체를 어떻게 할지 정한다
* ALL : 밑에 서술 모두 포함
* PERSIST : 부모 객체가 영속성을 지닐 때 자식 객체도 같이 영속성 지니게 함, 설정 시 자식 객체만 직접적으로 삭제 안 됨 
* REMOVE : 부모 객체가 삭제될 시 자식 객체 같이 삭제

PERSIST 자식 객체 관리 부모가 하는 느낌?

#### 컨트롤러 Controller
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

#### 저장소 Repository

    @Repository
    public interface repository extends JpaRepository <Entity, Primary Key Type> {}
    
#### 객체 Entity
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
         @Id //PRIMARY KEY
         @GeneratedValue(strategy = GenerationType.IDENTITY) //AUTO_INCREMENT
         private Long id;
         @NonNull //lombok
         @Column(nullable = false)
         private String name;
    }

객체 JSON으로 반환 시 양방향 관계 객체의 경우 순환 참조 문제가 발생된다
아래 어노테이션 이용해서 순환 참조 방지
    
    @JsonBackReference 
    @JsonManagedReference 

