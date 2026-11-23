ALTER TABLE public.users
ADD COLUMN IF NOT EXISTS email_address varchar(50);

