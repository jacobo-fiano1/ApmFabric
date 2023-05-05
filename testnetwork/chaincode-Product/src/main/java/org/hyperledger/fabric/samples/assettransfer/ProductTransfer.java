/*
 * SPDX-License-Identifier: Apache-2.0
 */

package org.hyperledger.fabric.samples.assettransfer;

import java.util.ArrayList;
import java.util.List;


import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.contract.ContractInterface;
import org.hyperledger.fabric.contract.annotation.Contact;
import org.hyperledger.fabric.contract.annotation.Contract;
import org.hyperledger.fabric.contract.annotation.Default;
import org.hyperledger.fabric.contract.annotation.Info;
import org.hyperledger.fabric.contract.annotation.License;
import org.hyperledger.fabric.contract.annotation.Transaction;
import org.hyperledger.fabric.shim.ChaincodeException;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.hyperledger.fabric.shim.ledger.KeyValue;
import org.hyperledger.fabric.shim.ledger.QueryResultsIterator;

import com.owlike.genson.Genson;

@Contract(
        name = "basic",
        info = @Info(
                title = "Product Transfer",
                description = "Transaccion de un producto dentro del ledger",
                version = "0.0.1-SNAPSHOT",
                license = @License(
                        name = "Apache 2.0 License",
                        url = "http://www.apache.org/licenses/LICENSE-2.0.html"),
                contact = @Contact(
                        email = "jacobo.fiano1@udc.es",
                        name = "APM",
                        url = "https://hyperledger.example.com")))
@Default
public final class ProductTransfer implements ContractInterface {

    private final Genson genson = new Genson();

    private enum ProductTransferErrors {
        PRODUCT_NOT_FOUND,
        PRODUCT_ALREADY_EXISTS
    }

    /**
     * Creates some initial assets on the ledger.
     *
     * @param ctx the transaction context
     */
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public void InitLedger(final Context ctx) {
        ChaincodeStub stub = ctx.getStub();

        CreateProduct(ctx, "product1", "car", "url1", "car description", "image1", "ownerA");
        CreateProduct(ctx, "product2", "boat", "url2", "boat description", "image1", "ownerB");
        CreateProduct(ctx, "product3", "bike", "url3", "bike description", "image1", "ownerC");
        CreateProduct(ctx, "product4", "house", "url4", "house description", "image1", "ownerA");
        CreateProduct(ctx, "product5", "flat", "url5", "flat description", "image1", "ownerB");
        CreateProduct(ctx, "product6", "train", "url6", "train description", "image1", "ownerB");
    }

    /**
     * Creates a new asset on the ledger.
     *
     * @param ctx the transaction context
     * @param assetID the ID of the new asset
     * @param color the color of the new asset
     * @param size the size for the new asset
     * @param owner the owner of the new asset
     * @param appraisedValue the appraisedValue of the new asset
     * @return the created asset
     */
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public Asset CreateProduct(final Context ctx, final String productID, final String name, final String url,
        final String description, final String image, final String owner) {
        ChaincodeStub stub = ctx.getStub();

        if (ProductExists(ctx, productID)) {
            String errorMessage = String.format("Product %s already exists", productID);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, ProductTransferErrors.PRODUCT_ALREADY_EXISTS.toString());
        }

        Product product = new Product(productID, name, url, description, image, owner);
        // Use Genson to convert the Asset into string, sort it alphabetically and serialize it into a json string
        String sortedJson = genson.serialize(product);
        stub.putStringState(productID, sortedJson);

        return product;
    }

    /**
     * Retrieves an asset with the specified ID from the ledger.
     *
     * @param ctx the transaction context
     * @param productID the ID of the asset
     * @return the asset found on the ledger if there was one
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public Asset ReadProduct(final Context ctx, final String productID) {
        ChaincodeStub stub = ctx.getStub();
        String productJSON = stub.getStringState(productID);

        if (productJSON == null || productJSON.isEmpty()) {
            String errorMessage = String.format("Product %s does not exist", productID);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, ProductTransferErrors.PRODUCT_NOT_FOUND.toString());
        }

        Asset asset = genson.deserialize(productJSON, Asset.class);
        return asset;
    }

    /**
     * Updates the properties of an asset on the ledger.
     *
     * @param ctx the transaction context
     * @param assetID the ID of the asset being updated
     * @param color the color of the asset being updated
     * @param size the size of the asset being updated
     * @param owner the owner of the asset being updated
     * @param appraisedValue the appraisedValue of the asset being updated
     * @return the transferred asset
     */
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public Asset UpdateProduct(final Context ctx, final String productID, final String name, final String url,
        final String description, final String image, final String owner) {
        ChaincodeStub stub = ctx.getStub();

        if (!ProductExists(ctx, productID)) {
            String errorMessage = String.format("Product %s does not exist", productID);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, ProductTransferErrors.PRODUCT_NOT_FOUND.toString());
        }

        Product newProduct = new Product(productID, name, url, description, image, owner);
        // Use Genson to convert the Asset into string, sort it alphabetically and serialize it into a json string
        String sortedJson = genson.serialize(newProduct);
        stub.putStringState(productID, sortedJson);
        return newProduct;
    }

    /**
     * Deletes asset on the ledger.
     *
     * @param ctx the transaction context
     * @param assetID the ID of the asset being deleted
     */
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public void DeleteProduct(final Context ctx, final String productID) {
        ChaincodeStub stub = ctx.getStub();

        if (!ProductExists(ctx, productID)) {
            String errorMessage = String.format("Product %s does not exist", productID);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, ProductTransferErrors.PRODUCT_NOT_FOUND.toString());
        }

        stub.delState(productID);
    }

    /**
     * Checks the existence of the asset on the ledger
     *
     * @param ctx the transaction context
     * @param productID the ID of the asset
     * @return boolean indicating the existence of the asset
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public boolean ProductExists(final Context ctx, final String productID) {
        ChaincodeStub stub = ctx.getStub();
        String assetJSON = stub.getStringState(productID);

        return (assetJSON != null && !assetJSON.isEmpty());
    }

    /**
     * Changes the owner of a asset on the ledger.
     *
     * @param ctx the transaction context
     * @param assetID the ID of the asset being transferred
     * @param newOwner the new owner
     * @return the old owner
     */
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public String TransferAsset(final Context ctx, final String productID, final String newOwner) {
        ChaincodeStub stub = ctx.getStub();
        String productJSON = stub.getStringState(productID);

        if (productJSON == null || productJSON.isEmpty()) {
            String errorMessage = String.format("Product %s does not exist", productID);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, ProductTransferErrors.PRODUCT_NOT_FOUND.toString());
        }

        Product product = genson.deserialize(productJSON, Product.class);

        Product newProduct = new Product(product.getAssetID(), product.getName(), product.getUrl(), product.getDescription, product.getImage(), newOwner);
        // Use a Genson to conver the Asset into string, sort it alphabetically and serialize it into a json string
        String sortedJson = genson.serialize(newProduct);
        stub.putStringState(productID, sortedJson);

        return product.getOwner();
    }

    /**
     * Retrieves all assets from the ledger.
     *
     * @param ctx the transaction context
     * @return array of assets found on the ledger
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public String GetAllProducts(final Context ctx) {
        ChaincodeStub stub = ctx.getStub();

        List<Product> queryResults = new ArrayList<Product>();

        // To retrieve all assets from the ledger use getStateByRange with empty startKey & endKey.
        // Giving empty startKey & endKey is interpreted as all the keys from beginning to end.
        // As another example, if you use startKey = 'asset0', endKey = 'asset9' ,
        // then getStateByRange will retrieve asset with keys between asset0 (inclusive) and asset9 (exclusive) in lexical order.
        QueryResultsIterator<KeyValue> results = stub.getStateByRange("", "");

        for (KeyValue result: results) {
            Product product = genson.deserialize(result.getStringValue(), Product.class);
            System.out.println(product);
            queryResults.add(product);
        }

        final String response = genson.serialize(queryResults);

        return response;
    }
}
