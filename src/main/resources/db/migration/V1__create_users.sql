create table users (
                       id uuid primary key default gen_random_uuid(),
                       name varchar(120) not null,
                       email varchar(180) not null,
                       password text,
                       role varchar(30) not null,
                       goal varchar(30) not null,
                       activity_level varchar(30) not null,
                       start_weight_kg double precision not null,
                       actual_weight_kg double precision not null,
                       target_weight_kg double precision not null,
                       weekly_goal_kg double precision,
                       created_at timestamp with time zone not null default now()
);

create unique index uk_users_email on users (lower(email));
