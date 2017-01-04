package org.flowable.idm.engine.test.api.identity;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.flowable.idm.api.Picture;
import org.flowable.idm.engine.impl.persistence.entity.UserEntityImpl;
import org.junit.Test;

/**
 * @author Arkadiy Gornovoy
 *
 */
public class UserEntityTest {

  @Test
  public void testSetPicture_pictureShouldBeSavedWhenNotNull() {
    TestableUserEntity userEntity = new TestableUserEntity();
    Picture picture = new Picture(null, null);
    // even though parameters were null, picture object is not null
    userEntity.setPicture(picture);
    assertTrue(userEntity.getHasSavePictureBeenCalled());
    assertFalse(userEntity.getHasDeletePictureBeenCalled());
  }

  @Test
  public void testSetPicture_pictureShouldBeDeletedWhenNull() {
    TestableUserEntity userEntity = new TestableUserEntity();
    userEntity.setPicture(null);
    assertTrue(userEntity.getHasDeletePictureBeenCalled());
  }

  @SuppressWarnings("serial")
  class TestableUserEntity extends UserEntityImpl {

    private boolean hasSavePictureBeenCalled;
    private boolean hasDeletePictureBeenCalled;

    @Override
    protected void savePicture(Picture picture) {
      setHasSavePictureBeenCalled(true);
    }

    @Override
    protected void deletePicture() {
      setHasDeletePictureBeenCalled(true);
    }

    public boolean getHasSavePictureBeenCalled() {
      return hasSavePictureBeenCalled;
    }

    public void setHasSavePictureBeenCalled(boolean hasSavePictureBeenCalled) {
      this.hasSavePictureBeenCalled = hasSavePictureBeenCalled;
    }

    public boolean getHasDeletePictureBeenCalled() {
      return hasDeletePictureBeenCalled;
    }

    public void setHasDeletePictureBeenCalled(boolean hasDeletePictureBeenCalled) {
      this.hasDeletePictureBeenCalled = hasDeletePictureBeenCalled;
    }

  }

}
