INSERT INTO page(title) VALUES('페이지1');
INSERT INTO page(title) VALUES('페이지2');
INSERT INTO page(title) VALUES('페이지3');

INSERT INTO todo(page_id,content) VALUES(1,'내용1');
INSERT INTO todo(page_id,content) VALUES(1,'내용2');
INSERT INTO todo(page_id,content) VALUES(1,'내용3');

INSERT INTO todo(page_id,content) VALUES(2,'내용4');
INSERT INTO todo(page_id,content) VALUES(2,'내용5');
INSERT INTO todo(page_id,content) VALUES(2,'내용6');

INSERT INTO todo(page_id,content) VALUES(3,'내용7');
INSERT INTO todo(page_id,content) VALUES(3,'내용8');
INSERT INTO todo(page_id,content) VALUES(3,'내용9');

-- 테이블 page,to_do --
--page의 요소 page_id,title--
--to_do의 요소 list_id,content,page_id --

--cd C:\Program Files\MySQL\MySQL Server 8.0\bin --
-- mysql -u root -p --
-- root --
-- use todolist; --
-- show tables; --
-- select * from page; --
-- select * from to_do; --