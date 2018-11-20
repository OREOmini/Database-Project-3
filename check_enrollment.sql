CREATE DEFINER=`root`@`localhost` PROCEDURE `enrollCheck`(
 in stuid char(20),
    in courseCode char(20),
    in curYear char(20),
    in curSemester char(5),
    out message char(150),
    out output int
    )
BEGIN

declare t_error int default 0;
declare continue handler for sqlexception set t_error=1;
START transaction;

/* satisfy pre and enroll < max*/
IF EXISTS(
SELECT R.UoSCode
from enroll_info R, transcript T
where T.StudId = stuid AND R.UoSCode = courseCode AND R.Year = curYear AND R.Semester = curSemester AND R.PrereqUoSCode = T.UoSCode AND (T.Grade = "CR" OR T. Grade = "P" OR T.Grade = "D") AND R.Enrollment < R.MaxEnrollment)
THEN
UPDATE uosoffering
SET Enrollment = Enrollment + 1
WHERE UoSCode = courseCode;
INSERT INTO transcript
VALUES(studid, courseCode, curSemester, curYear, NULL);
SET message = 'Enroll success!';
SET output = 1;


/* satisfy pre but excess max enrollment*/
ELSEIF EXISTS(
SELECT R.UoSCode
from enroll_info R, transcript T
where T.StudId = stuid AND R.UoSCode = courseCode AND R.Year = curYear AND R.Semester = curSemester AND R.PrereqUoSCode = T.UoSCode AND (T.Grade = "CR" OR T. Grade = "P" OR T.Grade = "D") AND (R.Enrollment > R.MaxEnrollment OR R.Enrollment = R.MaxEnrollment))
THEN
SET message = 'Enroll failed.The course is beyond maxenrollment!';
SET output = 3;

/* Not satisfy pre */
/* condition 1: not pass the prerequisite */
ELSEIF EXISTS
(
SELECT R.UoSCode
from enroll_info R, transcript T
where T.StudId = stuid AND (R.UoSCode = courseCode AND R.Year = curYear AND R.Semester = curSemester AND R.PrereqUoSCode = T.UoSCode AND (T.Grade is null OR T.Grade = "F"))
)
THEN
select R.PrereqUoSCode into message
from enroll_info R, transcript T
where T.StudId = stuid AND (R.UoSCode = courseCode AND R.PrereqUoSCode = T.UoSCode AND (T.Grade is null OR T.Grade = "F"));
/*SET message = 'Fail to enroll because you haven\'t pass the prerequisite';*/
SET output = 0;
/* condition 2: not select prereq */
ELSEIF EXISTS
(
select R.UoSCode
from enroll_info R, transcript T
where T.StudId = stuid AND R.UoSCode = courseCode AND R.Year = curYear AND R.Semester = curSemester AND R.PrereqUoSCode not in
(SELECT T.UoSCode
FROM transcript T)
group by R.PrereqUoSCode
)
THEN
select R.PrereqUoSCode into message
from enroll_info R, transcript T
where T.StudId = stuid AND R.UoSCode = courseCode AND R.PrereqUoSCode not in
(SELECT T.UoSCode
FROM transcript T)
group by R.PrereqUoSCode;
SET output = 0;
/*SET message = 'Fail to enroll because you haven\'t selected the prerequisite course';*/


/* No prereq */
ELSEIF EXISTS
(SELECT R.UoSCode
from enroll_info R, transcript T
where T.StudId = stuid AND R.UoSCode <> courseCode AND courseCode
IN
(SELECT U.UoSCode
FROM uosoffering U
where U.UoSCode = courseCode and U.Enrollment < U.MaxEnrollment
))
THEN
UPDATE uosoffering
SET Enrollment = Enrollment + 1
WHERE UoSCode = courseCode;
INSERT INTO transcript
VALUES(Username, courseCode, curSemester, curYear, NULL);
SET message = 'Enroll success!';
SET output = 1;
/* No prereq but beyond enrollment*/
ELSEIF EXISTS
(SELECT R.UoSCode
from enroll_info R, transcript T
where T.StudId = stuid AND R.UoSCode <> courseCode AND courseCode
IN
(SELECT U.UoSCode
FROM uosoffering U
where U.UoSCode = courseCode AND (U.enrollment > U.MaxEnrollment OR U.Enrollment = U.MaxEnrollment)
))
THEN
SET message = 'Enroll_failed.The course is beyond max enrollment!';
SET output = 3;
ELSEIF NOT EXISTS(
SELECT L.UoSCode
from lecture L
where L.UoSCode = courseCode
)
THEN
SET message = 'Course is not existed in the databse.';
SET output = 2;

END if;

if (t_error=1) 
then begin
set message='ERROR occurs, ready to rollback';
rollback;
end;
else begin 
commit;
end;
end if;
 

END