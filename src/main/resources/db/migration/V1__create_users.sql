create table users (
                       id uuid primary key default gen_random_uuid(),

                       auth0_id varchar(120) not null unique,
                       email varchar(180) not null,

                       full_name varchar(120),                -- ⬅️ name helyett ez

                       role varchar(30) not null default 'USER',

                       goal varchar(30),
                       activity_level varchar(30),

                       start_weight_kg double precision,
                       actual_weight_kg double precision,
                       target_weight_kg double precision,
                       weekly_goal_kg double precision,

                       height_cm integer,                     -- ⬅️ ÚJ

                       profile_completed boolean not null default false, -- ⬅️ ÚJ

                       created_at timestamp with time zone not null default now()
);

create unique index uk_users_auth0_id on users (auth0_id);
create unique index uk_users_email on users (lower(email));
