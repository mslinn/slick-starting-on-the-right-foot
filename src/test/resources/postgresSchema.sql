CREATE TABLE bank(
  id BIGSERIAL PRIMARY KEY,
  name varchar(200)
);
CREATE TABLE bankinfo(
  id BIGSERIAL,
  owner varchar(200),
  bank_id BIGINT REFERENCES bank(id),
  branches BIGINT
);
CREATE TABLE bankproduct(
  id BIGSERIAL PRIMARY KEY,
  name varchar(200),
  bank_id BIGINT references bank(id)
);
