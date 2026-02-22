create table users (
                       id uuid primary key default gen_random_uuid(),

                       auth0_id varchar(120) not null unique,

                       full_name varchar(120),

                       birth_date date,

                       gender varchar(30),

                       role varchar(30) not null default 'USER',

                       height_cm integer,

                       start_weight_kg double precision,
                       actual_weight_kg double precision,
                       target_weight_kg double precision,
                       weekly_goal_kg double precision,

                       activity_level varchar(30),
                       goal varchar(30),

                       created_at timestamp with time zone not null default now()
);

create unique index uk_users_auth0_id on users (auth0_id);
