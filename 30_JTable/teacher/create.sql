create table JDBCT(
    NO number(2) constraint JDBCT_PK primary key, 
    NAME varchar2(10), 
    PHONE varchar2(15), 
    RDATE date 
);

insert into JDBCT values(10, '홍길동', '01012341234', SYSDATE);
insert into JDBCT values(20, '이순신', '01012341235', SYSDATE);
insert into JDBCT values(30, '강감찬', '01012341236', SYSDATE);
insert into JDBCT values(40, '을지문덕', '01012341237', SYSDATE);

commit;