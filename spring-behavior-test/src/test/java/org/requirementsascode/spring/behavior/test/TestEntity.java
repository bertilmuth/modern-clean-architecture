package org.requirementsascode.spring.behavior.test;

import java.util.UUID;

class TestEntity{
	private TestEntityId id;
	private String content;

	public TestEntity(String content) {
		this(TestEntityId.of(UUID.randomUUID()), content);
	}

	public TestEntity(TestEntityId id, String content) {
		this.id = id;
		this.content = content;
	}

	public static class TestEntityId {
		final UUID uuid;

		private TestEntityId(UUID uuid) {
			this.uuid = uuid;
		}

		public static TestEntityId of(UUID uuid) {
			return new TestEntityId(uuid);
		}

		public UUID getUuid() {
			return uuid;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			TestEntityId other = (TestEntityId) obj;
			if (uuid == null) {
				if (other.uuid != null)
					return false;
			} else if (!uuid.equals(other.uuid))
				return false;
			return true;
		}
	}

	public TestEntityId getId() {
		return id;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
		;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((content == null) ? 0 : content.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TestEntity other = (TestEntity) obj;
		if (content == null) {
			if (other.content != null)
				return false;
		} else if (!content.equals(other.content))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "TestEntity [id=" + id + ", content=" + content + "]";
	}
}
