CREATE TABLE vle2 (
  `DATE_KEY` int(11) NOT NULL PRIMARY KEY,
  `CAL_DATE` date DEFAULT NULL,
  `CAL_DAY_NUM_YEAR` smallint(6) DEFAULT NULL,
  `CAL_DIM_WEEK_NUM` int(11) DEFAULT NULL,
  `CAL_WEEK_STR` varchar(80) DEFAULT NULL,
  `CAL_WEEK_YEAR_STR` varchar(80) DEFAULT NULL,
  `CAL_WEEK_NUM_YEAR` tinyint(4) DEFAULT NULL,
  `CAL_DIM_MONTH_NUM` int(11) DEFAULT NULL,
  `CAL_MONTH_STR` varchar(80) DEFAULT NULL,
  `CAL_MONTH_YEAR_STR` varchar(80) DEFAULT NULL,
  `CAL_MONTH_NUM_YEAR` tinyint(4) DEFAULT NULL,
  `CAL_DIM_QUARTER_NUM` int(11) DEFAULT NULL,
  `CAL_QUARTER_STR` varchar(80) DEFAULT NULL,
  `CAL_QUARTER_YEAR_STR` varchar(80) DEFAULT NULL,
  `CAL_QUARTER_NUM_YEAR` tinyint(4) DEFAULT NULL,
  `CAL_YEAR` int(11) DEFAULT NULL,
  `CAL_YEAR_STR` varchar(80) DEFAULT NULL,
  `ISO_DIM_WEEK_NUM` int(11) DEFAULT NULL,
  `ISO_WEEK_STR` varchar(80) DEFAULT NULL,
  `ISO_WEEK_YEAR_STR` varchar(80) DEFAULT NULL,
  `ISO_WEEK_NUM_YEAR` tinyint(4) DEFAULT NULL,
  `ISO_WEEK_YEAR` int(11) DEFAULT NULL,
  `LE_WHSE_CREATED_ON_DT` timestamp NULL DEFAULT NULL,
  `LE_DATETIME` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ;

CREATE PROCEDURE TIME_SP()
begin
                          
   declare c_sunday_index                     tinyint default 1;  
   declare c_saturday_index                   tinyint default 7;

   
   declare v_from_year                        smallint;
   declare v_to_year                          smallint;
   declare default_from_year                  smallint default 1995;
   declare default_to_year                    smallint default 2050;
   declare v_date_key                         integer;        
   declare v_date                             date;
   declare v_day                              tinyint;        
   declare v_day_of_week                      tinyint;        
   declare v_day_name                         varchar(50);    
   declare v_day_of_year                      smallint;       
   declare v_year_month_day_name              varchar(50);           
   declare v_week_of_year                     tinyint;        
   declare v_year_week_name                   varchar(50);    
   declare v_month_of_quarter                 tinyint;        
   declare v_month                            tinyint;        
   declare v_month_name                       varchar(50);    
   declare v_year_month_name                  varchar(50);    
   declare v_quarter                          tinyint;        
   declare v_year_quarter_name                varchar(50);    
   declare v_year                             smallint;       
   declare v_quarter_order                    integer;        
   declare v_month_order                      integer;        
   declare v_week_order                       integer;        
   declare v_day_order                        integer;        

   declare v_two_digit_month                  char(2);
   declare v_two_digit_day                    char(2);
   declare v_week_adjustment                  tinyint;

   set v_from_year = default_from_year;
	 set v_to_year = default_to_year;
	 
   truncate table vle2;
    
   set v_year = v_from_year;


   
                   
   set v_date = makedate(v_year, 1);
   /*This will set the date_key to -364 for 1995-01-01 so that joins to sales_fact_1997 are valid*/
   set v_date_key = to_days(v_date) - 729023;

   
   
   year_loop: while (v_year <= v_to_year) do 

       if (dayofweek(v_date) = c_sunday_index) then
	      set v_week_adjustment = 0;
	   else	   
	      set v_week_adjustment = 1;
       end if;

        set v_quarter = 1;
	   
	   quarter_loop: while (v_quarter <= 4) do
		   set v_month_of_quarter = 1; 	   
           month_loop: while (v_month_of_quarter <=3) do

                 set v_day = 1;

                            
			   day_loop: loop
				   
                   set v_day_of_week = dayofweek(v_date);
                   set v_day_name = dayname(v_date);                   
                   set v_day_of_year = dayofyear(v_date);
				   
                   set v_month = month(v_date);
                   set v_month_name = monthname(v_date);
				   
				   set v_year_month_name = concat(v_year, '-M', v_month);
                   set v_year_quarter_name = concat(v_year, '-Q', v_quarter);
				     
                   set v_week_of_year = week(v_date) + v_week_adjustment;
                   set v_year_week_name = concat(v_year, '-', 'W', v_week_of_year);

				   if (v_month < 10) then
				       set v_two_digit_month = concat('0',v_month);
				   else
				       set v_two_digit_month = v_month;
                   end if;

				   if (v_day < 10) then
				       set v_two_digit_day = concat('0',v_day);
				   else
				       set v_two_digit_day = v_day;
                   end if;
				   
				   set v_year_month_day_name = concat(v_year, '-', v_two_digit_month, '-', v_two_digit_day);
				   
				   set v_quarter_order = (v_year * 10) + v_quarter;
				   set v_month_order = (v_year * 100) + v_month;
				   set v_week_order = (v_year * 10000) + (v_month * 100) + v_week_of_year;
				   set v_day_order = (v_year * 1000) + v_day_of_year;
				   
                   insert into vle2 (
                          DATE_KEY,
                          CAL_DATE,
                          CAL_DAY_NUM_YEAR,
                          
                          CAL_DIM_WEEK_NUM,
                          CAL_WEEK_STR,
                          CAL_WEEK_YEAR_STR,
                          CAL_WEEK_NUM_YEAR,
                          
                          CAL_DIM_MONTH_NUM,
                          CAL_MONTH_STR,
                          CAL_MONTH_YEAR_STR,
                          CAL_MONTH_NUM_YEAR,
                          
                          CAL_DIM_QUARTER_NUM,
                          CAL_QUARTER_STR,
                          CAL_QUARTER_YEAR_STR,
                          CAL_QUARTER_NUM_YEAR,
                          
                          CAL_YEAR,
                          CAL_YEAR_STR,

                          ISO_DIM_WEEK_NUM,
					 ISO_WEEK_STR,
					 ISO_WEEK_YEAR_STR,
					 ISO_WEEK_NUM_YEAR,
					 ISO_WEEK_YEAR,
                          
                          LE_WHSE_CREATED_ON_DT,
                          LE_DATETIME
                          )
                   values 
                          (
                          v_date_key,
                          v_date,
                          v_day_of_year,
                          
                          v_week_order,
                          concat('W', v_week_of_year), 
					v_year_week_name, -- Make look like yyyy-Www
                          v_week_of_year,
                          
                          v_month_order,
                          concat('M', v_month), -- Make look like M01
                          v_year_month_name, -- Make look like 1997-M4
                          v_month,
                          
                          v_quarter_order,
                          concat('Q', v_quarter),
                          v_year_quarter_name, -- Make look like 1997-Q1
                          v_quarter,
                          
                          v_year,
                          v_year,
                          
                          yearweek(v_date,3),
                          concat('W', week(v_date,3)),
                          concat(floor(yearweek(v_date,3)/100), '-W', week(v_date,3)),
                          week(v_date,3),
                          floor(yearweek(v_date,3)/100),

                          '2020-02-06 06:28:58.0',
                          '2020-02-06 06:28:58.0'
                          );
                          
                          
                          if (v_date = last_day(v_date)) then
             set v_month_of_quarter = v_month_of_quarter + 1;
					   set v_date = ADDDATE(v_date, 1);
					   set v_date_key = v_date_key + 1;
					   leave day_loop;
                   end if;	
 
                   set v_day = v_day + 1;
                   set v_date = ADDDATE(v_date, 1);
				   set v_date_key = v_date_key + 1;
				   
				 
			   end loop day_loop;
			   
           end while month_loop;
	       
		   set v_quarter = v_quarter + 1;    
	   end while quarter_loop;
       
	   set v_year = v_year + 1;
   end while year_loop;
end ;;

call time_sp();


insert into vle2 (
                          DATE_KEY,
                          CAL_DIM_WEEK_NUM,
					 CAL_DIM_MONTH_NUM,
					 CAL_DIM_QUARTER_NUM,
					 CAL_YEAR
					 
                          )
                   values (-99999,
                   -99999999,
                   -99999999,
                   -99999999,
                   -99999999);   
                   
insert into vle2 (
                          DATE_KEY,
                          CAL_WEEK_STR,
                          CAL_WEEK_YEAR_STR,
                          CAL_MONTH_STR,
                          CAL_MONTH_YEAR_STR,
                          CAL_QUARTER_STR,
                          CAL_QUARTER_YEAR_STR,
                          CAL_YEAR_STR,
                          
                          CAL_DIM_WEEK_NUM,
					 CAL_DIM_MONTH_NUM,
					 CAL_DIM_QUARTER_NUM,
					 CAL_YEAR,
                          
                          LE_WHSE_CREATED_ON_DT,
                          LE_DATETIME
                          )
                   values (-99998, 
                   'Date Before 01/01/1995',
                   'Date Before Calendar Week  1995-W1',
                   'Date Before 01/01/1995',
                   'Date Before Calendar Month  1995-M1',
                   'Date Before 01/01/1995',
                   'Date Before Calendar Quarter 1995-Q1',
                   'Date Before Calendar Year 1995',
                   -99999998,
                   -99999998,
                   -99999998,
                   -99999998,
                   '2008-11-29 08:59:01.0',
                   '2017-12-18 01:43:58.0'
                   );    

insert into vle2 (
                          DATE_KEY,
                          CAL_WEEK_STR,
                          CAL_WEEK_YEAR_STR,
                          CAL_MONTH_STR,
                          CAL_MONTH_YEAR_STR,
                          CAL_QUARTER_STR,
                          CAL_QUARTER_YEAR_STR,
                          CAL_YEAR_STR,
					 CAL_DIM_WEEK_NUM,
					 CAL_DIM_MONTH_NUM,
					 CAL_DIM_QUARTER_NUM,
					 CAL_YEAR,
                          LE_WHSE_CREATED_ON_DT,
                          LE_DATETIME
                          )
                   values (99999, 
                   'Date After 12/31/2050',
                   'Date After Calendar Week  2050-W53',
                   'Date After 12/31/2050',
                   'Date After Calendar Month  2050-M12',
                   'Date After 12/31/2050',
                   'Date After Calendar Quarter 2050-Q4',
                   'Date After Calendar Year 2050',
                   99999999,
                   99999999,
                   99999999,
                   99999999,
                   '2008-11-29 08:59:01.0',
                   '2017-12-18 01:43:58.0'
                   );     

                  
                   
create table vle_calendar2 as select * from vle_calendar;

update vle2 x,
(select y.DATE_KEY as y0, y.LE_WHSE_CREATED_ON_DT y1, LE_DATETIME y2
from vle_calendar2 y
) lookup
set x.LE_WHSE_CREATED_ON_DT = lookup.y1, x.LE_DATETIME = lookup.y2
where x.DATE_KEY = lookup.y0;

drop table vle_calendar;
rename table vle2 to vle_calendar;

          