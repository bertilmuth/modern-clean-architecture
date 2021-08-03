package org.requirementsascode.spring.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.requirementsascode.spring.test.TestEntity.TestEntityId;

class TestRepositoryTest {
	private TestRepository<TestEntity, TestEntityId> repository;

	@BeforeEach
	void setup() {
		this.repository = newTestRepository();
	}

	@Test
	void createsEmptyRepository() {
		// Act
		final long count = repository.count();

		// Assert
		assertEquals(0, count);
	}

	@Test
	void savesOneEntity() {
		// Arrange
		final TestEntity entity1 = new TestEntity("entity1");

		// Act
		TestEntity savedEntity1 = repository.save(entity1);

		// Assert
		final Iterator<TestEntity> it = repository.findAll().iterator();
		assertEquals(1, repository.count());
		assertEquals(savedEntity1, it.next());
	}

	@Test
	void savesEntityTwicePreservingIds() {
		// Arrange
		final TestEntity entity1 = new TestEntity("entity1");

		// Act
		TestEntity savedEntity1 = repository.save(entity1);
		TestEntity savedEntity2 = repository.save(entity1);

		// Assert
		assertNotNull(savedEntity1.getId());
		assertEquals(1, repository.count());
		assertEquals(savedEntity1.getId(), savedEntity2.getId());
	}

	@Test
	void savesTwoEntitiesIndividually() {
		// Arrange
		final TestEntity entity1 = new TestEntity("entity1");
		final TestEntity entity2 = new TestEntity("entity2");

		// Act
		TestEntity savedEntity1 = repository.save(entity1);
		TestEntity savedEntity2 = repository.save(entity2);

		// Assert
		final Iterator<TestEntity> it = repository.findAll().iterator();
		assertEquals(2, repository.count());
		assertEquals(savedEntity1, it.next());
		assertEquals(savedEntity2, it.next());
		assertNotEquals(savedEntity1.getId(), savedEntity2.getId());
	}

	@Test
	void savesTwoEntitiesWithSaveAll() {
		// Arrange
		final TestEntity entity1 = new TestEntity("entity1");
		final TestEntity entity2 = new TestEntity("entity2");

		// Act
		final Iterator<TestEntity> it = repository.saveAll(Arrays.asList(entity1, entity2)).iterator();

		// Assert
		assertEquals(2, repository.count());
		assertEquals(entity1.getContent(), it.next().getContent());
		assertEquals(entity2.getContent(), it.next().getContent());
	}

	@Test
	void doesntFindEntityInEmptyRepository() {
		final Optional<TestEntity> entity = repository.findById(randomId());

		// Assert
		assertFalse(entity.isPresent());
	}

	@Test
	void findsOneEntity() {
		// Arrange
		final TestEntity entity1 = new TestEntity("entity1");
		TestEntity savedEntity = repository.save(entity1);
		TestEntityId savedEntityId = savedEntity.getId();

		// Act
		final Optional<TestEntity> foundEntity = repository.findById(savedEntityId);

		// Assert
		assertEquals(savedEntity, foundEntity.get());
	}

	@Test
	void findsTwoEntities() {
		// Arrange
		final TestEntity entity1 = new TestEntity("entity1");
		final TestEntity entity2 = new TestEntity("entity2");
		final TestEntity savedEntity1 = repository.save(entity1);
		final TestEntity savedEntity2 = repository.save(entity2);
		TestEntityId savedEntityId1 = savedEntity1.getId();
		TestEntityId savedEntityId2 = savedEntity2.getId();

		// Act
		Optional<TestEntity> foundEntity1 = repository.findById(savedEntityId1);
		Optional<TestEntity> foundEntity2 = repository.findById(savedEntityId2);

		// Assert
		assertEquals(savedEntity1, foundEntity1.get());
		assertEquals(savedEntity2, foundEntity2.get());
	}

	@Test
	void nothingExistsInEmptyRepository() {
		// Act
		final boolean exists = repository.existsById(randomId());

		// Assert
		assertFalse(exists);
	}

	@Test
	void oneEntityExists() {
		// Arrange
		final TestEntity entity1 = new TestEntity("entity1");
		TestEntity savedEntity = repository.save(entity1);
		TestEntityId savedEntityId = savedEntity.getId();

		// Act
		final boolean savedEntityExists = repository.existsById(savedEntityId);

		// Assert
		assertTrue(savedEntityExists);
	}

	@Test
	void doesntFindEntitiesInEmptyRepository() {
		// Arrange
		List<TestEntityId> entityIds = Arrays.asList(randomId(), randomId());

		// Act
		Iterator<TestEntity> it = repository.findAllById(entityIds).iterator();

		// Assert
		assertFalse(it.hasNext());
	}

	@Test
	void findsTwoEntitiesAmongThree() {
		// Arrange
		final TestEntity entity1 = new TestEntity("entity1");
		final TestEntity entity2 = new TestEntity("entity2");
		final TestEntity entity3 = new TestEntity("entity3");
		final TestEntity savedEntity1 = repository.save(entity1);
		repository.save(entity2);
		final TestEntity savedEntity3 = repository.save(entity3);
		List<TestEntityId> entityIds = Arrays.asList(savedEntity1.getId(), savedEntity3.getId());

		// Act
		Iterator<TestEntity> it = repository.findAllById(entityIds).iterator();

		// Assert
		assertEquals(savedEntity1, it.next());
		assertEquals(savedEntity3, it.next());
		assertFalse(it.hasNext());
	}

	@Test
	void doesntDeleteNonExistingElementById() {
		// Act
		repository.deleteById(randomId());

		// Assert
		assertEquals(0, repository.count());
	}

	@Test
	void deletesOneElement() {
		// Arrange
		final TestEntity entity1 = new TestEntity("entity1");
		final TestEntityId savedEntityId1 = repository.save(entity1).getId();

		// Act
		repository.deleteById(savedEntityId1);

		// Assert
		assertEquals(0, repository.count());
	}

	@Test
	void deletesOneEntityAmongThreeById() {
		// Arrange
		final TestEntity entity1 = new TestEntity("entity1");
		final TestEntity entity2 = new TestEntity("entity2");
		final TestEntity entity3 = new TestEntity("entity3");
		final TestEntity savedEntity1 = repository.save(entity1);
		final TestEntity savedEntity2 = repository.save(entity2);
		final TestEntity savedEntity3 = repository.save(entity3);
		repository.save(entity3);

		// Act
		repository.deleteById(savedEntity2.getId());

		// Assert
		final Iterator<TestEntity> it = repository.findAll().iterator();
		assertEquals(2, repository.count());
		assertEquals(savedEntity1, it.next());
		assertEquals(savedEntity3, it.next());
	}

	@Test
	void doesntDeleteNonExistingElementByEntity() {
		// Arrange
		final TestEntity entity1 = new TestEntity("entity1");

		// Act
		repository.delete(entity1);

		// Assert
		assertEquals(0, repository.count());
	}

	@Test
	void deletesOneEntityAmongThreeByEntity() {
		// Arrange
		final TestEntity entity1 = new TestEntity("entity1");
		final TestEntity entity2 = new TestEntity("entity2");
		final TestEntity entity3 = new TestEntity("entity3");
		final TestEntity savedEntity1 = repository.save(entity1);
		final TestEntity savedEntity2 = repository.save(entity2);
		final TestEntity savedEntity3 = repository.save(entity3);
		repository.save(entity3);

		// Act
		repository.delete(savedEntity2);

		// Assert
		final Iterator<TestEntity> it = repository.findAll().iterator();
		assertEquals(2, repository.count());
		assertEquals(savedEntity1, it.next());
		assertEquals(savedEntity3, it.next());
	}

	@Test
	void deletesTwoEntitiesAmongThreeByTheirIds() {
		// Arrange
		final TestEntity entity1 = new TestEntity("entity1");
		final TestEntity entity2 = new TestEntity("entity2");
		final TestEntity entity3 = new TestEntity("entity3");
		final TestEntity savedEntity1 = repository.save(entity1);
		final TestEntity savedEntity2 = repository.save(entity2);
		final TestEntity savedEntity3 = repository.save(entity3);
		List<TestEntityId> entityIds = Arrays.asList(savedEntity1.getId(), savedEntity3.getId());

		// Act
		repository.deleteAllById(entityIds);

		// Assert
		final Iterator<TestEntity> it = repository.findAll().iterator();
		assertEquals(savedEntity2, it.next());
		assertFalse(it.hasNext());
	}

	@Test
	void deletesTwoEntitiesAmongThreeByEntity() {
		// Arrange
		final TestEntity entity1 = new TestEntity("entity1");
		final TestEntity entity2 = new TestEntity("entity2");
		final TestEntity entity3 = new TestEntity("entity3");
		final TestEntity savedEntity1 = repository.save(entity1);
		final TestEntity savedEntity2 = repository.save(entity2);
		final TestEntity savedEntity3 = repository.save(entity3);
		List<TestEntity> entities = Arrays.asList(savedEntity1, savedEntity3);

		// Act
		repository.deleteAll(entities);

		// Assert
		final Iterator<TestEntity> it = repository.findAll().iterator();
		assertEquals(savedEntity2, it.next());
		assertFalse(it.hasNext());
	}

	@Test
	void deletesAllEntities() {
		// Arrange
		final TestEntity entity1 = new TestEntity("entity1");
		final TestEntity entity2 = new TestEntity("entity2");
		final TestEntity entity3 = new TestEntity("entity3");
		repository.save(entity1);
		repository.save(entity2);
		repository.save(entity3);

		// Act
		repository.deleteAll();

		// Assert
		assertEquals(0, repository.count());
	}

	@Test
	void savedEntityIsDifferentFromLoadedEntity() {
		// Arrange
		final TestEntity entity1 = new TestEntity("entity1");

		// Act
		TestEntity savedEntity1 = repository.save(entity1);
		savedEntity1.setContent("unsavedContent");

		// Assert
		final Iterator<TestEntity> it = repository.findAll().iterator();
		final TestEntity loadedEntity1 = it.next();
		assertEquals("entity1", loadedEntity1.getContent());
	}

	@Test
	void reloadsEntityFoundById() {
		// Arrange
		final TestEntity entity1 = new TestEntity("entity1");
		repository.save(entity1);

		// Act
		TestEntity loadedEntity = repository.findById(entity1.getId()).get();
		loadedEntity.setContent("unsavedContent");
		TestEntity reloadedEntity = repository.findById(entity1.getId()).get();

		// Assert
		assertEquals("unsavedContent", loadedEntity.getContent());
		assertEquals("entity1", reloadedEntity.getContent());
	}

	@Test
	void reloadsEntityFoundByFindAll() {
		// Arrange
		final TestEntity entity1 = new TestEntity("entity1");
		repository.save(entity1);

		// Act
		TestEntity loadedEntity = repository.findAll().iterator().next();
		loadedEntity.setContent("unsavedContent");
		TestEntity reloadedEntity = repository.findAll().iterator().next();

		// Assert
		assertEquals("unsavedContent", loadedEntity.getContent());
		assertEquals("entity1", reloadedEntity.getContent());
	}

	private TestEntityId randomId() {
		TestEntityId randomId = TestEntityId.of(UUID.randomUUID());
		return randomId;
	}

	private TestRepository<TestEntity, TestEntityId> newTestRepository() {
		return new TestRepository<>(new TestEntityUuidProvider());
	}

	private class TestEntityUuidProvider implements EntityAccess<TestEntity, TestEntityId> {

		@Override
		public TestEntityId idOf(TestEntity entity) {
			return entity.getId();
		}

		@Override
		public TestEntityId nextId() {
			return TestEntityId.of(UUID.randomUUID());
		}

		@Override
		public TestEntity copyWithId(TestEntity entity, TestEntityId id) {
			return new TestEntity(id, entity.getContent());
		}

	}
}
