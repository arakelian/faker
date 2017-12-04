/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.arakelian.fake.model;

import java.time.ZonedDateTime;

import com.arakelian.core.utils.DateUtils;
import com.arakelian.core.utils.MoreStringUtils;
import com.arakelian.dao.feature.HasId;
import com.arakelian.dao.feature.HasMutableTimestamp;

public class Person implements HasId, HasMutableTimestamp, Comparable<Person> {
    public static Person create() {
        return new Person().withId(MoreStringUtils.shortUuid()).updated();
    }

    private String id;
    private String firstName;
    private String lastName;
    private Gender gender;
    private String position;
    private String comments;
    private ZonedDateTime created;

    private ZonedDateTime updated;

    public Person() {
    }

    @Override
    public int compareTo(final Person o) {
        if (o == null) {
            return +1;
        }
        if (id == null) {
            if (o.id == null) {
                return 0;
            }
            return -1;
        }
        return id.compareTo(o.id);
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Person other = (Person) obj;
        if (comments == null) {
            if (other.comments != null) {
                return false;
            }
        } else if (!comments.equals(other.comments)) {
            return false;
        }
        if (created == null) {
            if (other.created != null) {
                return false;
            }
        } else if (!created.equals(other.created)) {
            return false;
        }
        if (firstName == null) {
            if (other.firstName != null) {
                return false;
            }
        } else if (!firstName.equals(other.firstName)) {
            return false;
        }
        if (gender != other.gender) {
            return false;
        }
        if (id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!id.equals(other.id)) {
            return false;
        }
        if (lastName == null) {
            if (other.lastName != null) {
                return false;
            }
        } else if (!lastName.equals(other.lastName)) {
            return false;
        }
        if (position == null) {
            if (other.position != null) {
                return false;
            }
        } else if (!position.equals(other.position)) {
            return false;
        }
        if (updated == null) {
            if (other.updated != null) {
                return false;
            }
        } else if (!updated.equals(other.updated)) {
            return false;
        }
        return true;
    }

    public final String getComments() {
        return comments;
    }

    @Override
    public final ZonedDateTime getCreated() {
        return created;
    }

    public final String getFirstName() {
        return firstName;
    }

    public final Gender getGender() {
        return gender;
    }

    @Override
    public final String getId() {
        return id;
    }

    public final String getLastName() {
        return lastName;
    }

    public final String getPosition() {
        return position;
    }

    @Override
    public final ZonedDateTime getUpdated() {
        return updated;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (comments == null ? 0 : comments.hashCode());
        result = prime * result + (created == null ? 0 : created.hashCode());
        result = prime * result + (firstName == null ? 0 : firstName.hashCode());
        result = prime * result + (gender == null ? 0 : gender.hashCode());
        result = prime * result + (id == null ? 0 : id.hashCode());
        result = prime * result + (lastName == null ? 0 : lastName.hashCode());
        result = prime * result + (position == null ? 0 : position.hashCode());
        result = prime * result + (updated == null ? 0 : updated.hashCode());
        return result;
    }

    public final void setComments(final String comments) {
        this.comments = comments;
    }

    @Override
    public final void setCreated(final ZonedDateTime created) {
        this.created = created;
    }

    public final void setFirstName(final String firstName) {
        this.firstName = firstName;
    }

    public final void setGender(final Gender gender) {
        this.gender = gender;
    }

    public final void setId(final String id) {
        this.id = id;
    }

    public final void setLastName(final String lastName) {
        this.lastName = lastName;
    }

    public final void setPosition(final String title) {
        this.position = title;
    }

    @Override
    public final void setUpdated(final ZonedDateTime updated) {
        this.updated = updated;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("Person [");
        if (id != null) {
            builder.append("id=");
            builder.append(id);
            builder.append(", ");
        }
        if (firstName != null) {
            builder.append("firstName=");
            builder.append(firstName);
            builder.append(", ");
        }
        if (lastName != null) {
            builder.append("lastName=");
            builder.append(lastName);
            builder.append(", ");
        }
        if (position != null) {
            builder.append("position=");
            builder.append(position);
            builder.append(", ");
        }
        if (created != null) {
            builder.append("created=");
            builder.append(created);
            builder.append(", ");
        }
        if (updated != null) {
            builder.append("updated=");
            builder.append(updated);
        }
        builder.append("]");
        return builder.toString();
    }

    public Person updated() {
        final ZonedDateTime now = DateUtils.nowWithZoneUtc();
        if (created == null) {
            created = now;
        }
        updated = now;
        return this;
    }

    public final Person withComments(final String comments) {
        setComments(comments);
        return this;
    }

    public final Person withCreated(final ZonedDateTime created) {
        setCreated(created);
        return this;
    }

    public final Person withFirstName(final String firstName) {
        setFirstName(firstName);
        return this;
    }

    public final Person withGender(final Gender gender) {
        setGender(gender);
        return this;
    }

    public final Person withId(final String id) {
        setId(id);
        return this;
    }

    public final Person withLastName(final String lastName) {
        setLastName(lastName);
        return this;
    }

    public final Person withPosition(final String title) {
        setPosition(title);
        return this;
    }

    public final Person withUpdated(final ZonedDateTime updated) {
        setUpdated(updated);
        return this;
    }
}
