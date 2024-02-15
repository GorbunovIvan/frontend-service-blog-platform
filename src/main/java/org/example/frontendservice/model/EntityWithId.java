package org.example.frontendservice.model;

public interface EntityWithId<ID> {

    ID getId();
    void setId(ID id);

    /**
     * Creates dummy-object of provided type having provided ID
     * @param id  ID of an entity
     * @param entityClass Type of entity
     * @return Dummy-object of provided type having provided ID
     */
    static <ID> EntityWithId<?> createEntityWithId(ID id, Class<? extends EntityWithId<ID>> entityClass) {

        EntityWithId<?> entity = null;

        if (entityClass.equals(User.class)) {
            entity = new User((Long) id);
        }
        if (entityClass.equals(Post.class)) {
            entity = new Post((Long) id);
        }
        if (entityClass.equals(Comment.class)) {
            entity = new Comment((String) id);
        }

        if (entity == null) {
            throw new ClassCastException("Unknown class '" + entityClass.getSimpleName() + "'");
        }

        return entity;
    }
}
