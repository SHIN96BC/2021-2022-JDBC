create table JDBCT(
    NO number(2) constraint JDBCT_PK primary key, 
    NAME varchar2(10), 
    PHONE varchar2(15), 
    RDATE date 
);

insert into JDBCT values(10, 'ȫ�浿', '01012341234', SYSDATE);
insert into JDBCT values(20, '�̼���', '01012341235', SYSDATE);
insert into JDBCT values(30, '������', '01012341236', SYSDATE);
insert into JDBCT values(40, '��������', '01012341237', SYSDATE);

commit;