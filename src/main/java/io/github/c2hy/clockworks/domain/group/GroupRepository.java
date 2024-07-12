package io.github.c2hy.clockworks.domain.group;

import java.util.Optional;

public interface GroupRepository {
    Optional<Group> findById(String groupId);

    void save(Group group);
}
