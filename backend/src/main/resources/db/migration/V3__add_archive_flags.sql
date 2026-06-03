alter table customers
    add column archived boolean not null default false,
    add column archived_at timestamp(6);

alter table jewellery_items
    add column archived boolean not null default false,
    add column archived_at timestamp(6);

create index idx_customers_archived on customers (archived);
create index idx_jewellery_items_archived on jewellery_items (archived);
