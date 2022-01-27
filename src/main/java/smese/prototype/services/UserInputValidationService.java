package smese.prototype.services;

import smese.prototype.models.KeyAspectsModel;
import smese.prototype.models.UserInputModel;

public interface UserInputValidationService {

    KeyAspectsModel handleUserInputKeywordsAndFile(UserInputModel userInputModel, KeyAspectsModel keyAspectsModel, String keywords) throws Exception;
}
