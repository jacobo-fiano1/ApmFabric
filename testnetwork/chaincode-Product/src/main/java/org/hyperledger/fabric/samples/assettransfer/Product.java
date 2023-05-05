/*
 * SPDX-License-Identifier: Apache-2.0
 */

package org.hyperledger.fabric.samples.assettransfer;

import java.util.Objects;

import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

import com.owlike.genson.annotation.JsonProperty;

@DataType()
public final class Product {

    @Property()
    private final String productID;

    @Property()
    private final String name;

    @Property()
    private final String url;

    @Property()
    private final String description;

    @Property()
    private final String image;

    @Property()
    private final String owner;

    public String getProductID() {
        return productID;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public String getDescription() {
        return description;
    }

    public String getImage() {
        return image;
    }

    public String getOwner() {
        return owner;
    }

    public String getOwner() {
        return owner;
    }

    public Product(@JsonProperty("productID") final String productID, @JsonProperty("name") final String name,
            @JsonProperty("url") final String url, @JsonProperty("description") final String description, 
            @JsonProperty("image") final String image, @JsonProperty("owner") final String owner) {
        this.productID = productID;
        this.url = url;
        this.description = description;
        this.image = image;
        this.owner = owner;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if ((obj == null) || (getClass() != obj.getClass())) {
            return false;
        }

        Product other = (Product) obj;

        return Objects.deepEquals(
                new String[] {getProductID(), getName(), getOwner()},
                new String[] {other.getProductID(), other.getName(), other.getOwner()});
    }

    @Override
    public int hashCode() {
        return Objects.hash(getProductID(), getUrl(), getDescription(), getOwner(), getOwner());
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "@" + Integer.toHexString(hashCode()) + " [productID=" + productID + ", url="
                + url + ", name=" + name + ", description=" + description + ", owner=" + owner + "]";
    }
}
