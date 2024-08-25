alter table festival
    add column content_id varchar(255);

alter table festival
    modify column fee varchar(1000);

alter table festival
    modify column playtime varchar(1000);

alter table festival
    modify column homepage_url varchar(2083);

alter table festival
    modify column instagram_url varchar(2083);

alter table festival
    modify column ticket_link varchar(2083);

alter table festival
    modify column tip varchar(1000);
