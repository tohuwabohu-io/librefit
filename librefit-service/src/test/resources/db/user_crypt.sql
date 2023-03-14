create extension pgcrypto;

create function encrypt_pwd()
    returns trigger AS '
begin
    NEW.password := crypt(NEW.password, gen_salt(''bf''));
return NEW;
end;
' language plpgsql;

create trigger password_trigger before insert on libre_user for each row execute procedure encrypt_pwd();