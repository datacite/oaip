require 'spec_helper'

describe "XML Transformation" do

  context "against schema 4" do
    let(:template) { Nokogiri::XSLT(File.read('/home/app/src/main/webapp/xsl/kernel4_to_oaidc.xsl')) }

    describe "Good XML" do
      let(:document) { Nokogiri::XML(File.read('/home/app/spec/fixtures/_10.14279.xml'), nil, 'UTF-8', &:noblanks) }
        it 'generating formatted content' do
          # transformed_document = template.apply_to(document)
          puts template.transform(document)
          expect {template.transform(document)}.not_to raise_error
        end
    end

    describe "Bad XML" do
      let(:document) { Nokogiri::XML(File.read('/home/app/spec/fixtures/_10.23650.xml'), nil, 'UTF-8', &:noblanks) }
        it 'generating formatted content' do
          # transformed_document = template.apply_to(document)
          # puts template.transform(document)
          expect {template.transform(document)}.not_to raise_error
        end
    end

    describe "Bad XML changed namespaces to kernel 3" do
      let(:document) { Nokogiri::XML(File.read('/home/app/spec/fixtures/_10.23656.xml'), nil, 'UTF-8', &:noblanks) }
        it 'generating formatted content' do
          # transformed_document = template.apply_to(document)
          puts template.transform(document)
          expect {template.transform(document)}.not_to raise_error
        end
    end
  end

  context "against schema 3" do
    let(:template) { Nokogiri::XSLT(File.read('/home/app/src/main/webapp/xsl/kernel3_to_oaidc.xsl')) }


    describe "Bad XML" do
      let(:document) { Nokogiri::XML(File.read('/home/app/spec/fixtures/_10.23650.xml'), nil, 'UTF-8', &:noblanks) }
        it 'generating formatted content' do
          # transformed_document = template.apply_to(document)
          puts template.transform(document)
          expect {template.transform(document)}.not_to raise_error
        end
    end

    describe "Bad XML changed namespaces to kernel 3" do
      let(:document) { Nokogiri::XML(File.read('/home/app/spec/fixtures/_10.23656.xml'), nil, 'UTF-8', &:noblanks) }
        it 'generating formatted content' do
          # transformed_document = template.apply_to(document)
          puts template.transform(document)
          expect {template.transform(document)}.not_to raise_error
        end
    end
    describe "Bad XML changed to namespaces kernel 3 no titles" do
      let(:document) { Nokogiri::XML(File.read('/home/app/spec/fixtures/_10.23657.xml'), nil, 'UTF-8', &:noblanks) }
        it 'generating formatted content' do
          # transformed_document = template.apply_to(document)
          puts template.transform(document)
          expect {template.transform(document)}.not_to raise_error
        end
    end

    describe "Bad XML no titles no multiple names spaces" do
      let(:document) { Nokogiri::XML(File.read('/home/app/spec/fixtures/_10.23658.xml'), nil, 'UTF-8', &:noblanks) }
        it 'generating formatted content' do
          # transformed_document = template.apply_to(document)
          puts template.transform(document)
          expect {template.transform(document)}.not_to raise_error
        end
    end

  end
end
