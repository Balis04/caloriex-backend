create table users (
                       id uuid primary key default gen_random_uuid(),
                       auth0_id varchar(120) not null unique,
                       full_name varchar(120) not null,
                       birth_date date not null ,
                       gender varchar(30) not null,
                       role varchar(30) not null default 'USER',
                       height_cm integer not null ,
                       start_weight_kg double precision not null ,
                       actual_weight_kg double precision not null ,
                       target_weight_kg double precision not null ,
                       weekly_goal_kg double precision not null ,
                       activity_level varchar(30) not null ,
                       goal varchar(30) not null ,
                       email VARCHAR(255) not null ,
                       created_at timestamp with time zone not null default now()
);

create unique index uk_users_auth0_id on users (auth0_id);
